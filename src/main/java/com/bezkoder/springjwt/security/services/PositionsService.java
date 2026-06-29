package com.bezkoder.springjwt.security.services;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.bezkoder.springjwt.models.Position.*;
import com.bezkoder.springjwt.payload.request.Ingredients.IngAmountRequestDto;
import com.bezkoder.springjwt.payload.request.Position.PositionCreateDto;
import com.bezkoder.springjwt.payload.response.Positions.PositionMinDto;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.security.Exceptions.CategoryNotFoundException;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import com.bezkoder.springjwt.security.Exceptions.PositionCreateException;
import com.bezkoder.springjwt.security.Exceptions.PositionDeleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class PositionsService {
    private final PositionsRepos positionsRepos;
    private final CategoriesRepos categoriesRepos;
    private final PositionAmountRepos positionAmountRepos;
    private final IIngredientsRepos ingredientsRepos;
    private final IIngAmountRepos ingAmountRepos;
    private final IUnitRepos unitRepos;
    private final IIngCategoryRepos iIngCategoryRepos;
    private final BlobContainerClient positionImagesContainerClient;
    private final String positionImagesContainerUrl;

    @Autowired
    public PositionsService(PositionsRepos positionsRepos, CategoriesRepos categoriesRepos, PositionAmountRepos positionAmountRepos, IIngredientsRepos ingredientsRepos, IIngAmountRepos iIngAmountRepos, IUnitRepos unitRepos, IIngCategoryRepos iIngCategoryRepos,
                            @Value("${azure.storage.connection-string:}") String azureConnectionString,
                            @Value("${azure.storage.positions.container-name:posimgs}") String positionImagesContainerName,
                            @Value("${azure.storage.positions.container-url:https://bopositionsimg.blob.core.windows.net/posimgs}") String positionImagesContainerUrl) {
        this.positionsRepos = positionsRepos;
        this.categoriesRepos = categoriesRepos;
        this.positionAmountRepos = positionAmountRepos;
        this.ingredientsRepos = ingredientsRepos;
        this.ingAmountRepos = iIngAmountRepos;
        this.unitRepos = unitRepos;
        this.iIngCategoryRepos = iIngCategoryRepos;
        this.positionImagesContainerUrl = positionImagesContainerUrl;
        this.positionImagesContainerClient = azureConnectionString == null || azureConnectionString.isBlank()
                ? null
                : new BlobContainerClientBuilder()
                .connectionString(azureConnectionString)
                .containerName(positionImagesContainerName)
                .buildClient();
    }

    public List<Position> getPositions(long categoryId){
        List<Position> res = positionsRepos.findAllByCategoryId(categoryId);
        if(res.isEmpty()){
            throw new NoContentException("No positions found");
        }
        return res;
    }

    public List<Position> getAllPositions(long userId){
        List<Position> res = positionsRepos.findAllByCategoryUserId(userId);

        if(res.isEmpty()){
            throw new NoContentException("No positions found");
        }

        return res;
    }

    public List<PositionMinDto> getAllPositionsByCategoryId(Long categoryId) {
        List<Position> res;
        if(categoryId == 0){
            if(this.categoriesRepos.findAll().isEmpty()){
                throw new NoContentException("No categories found");
            }
            categoryId = this.categoriesRepos.findAll().get(0).getId();
        }
        res = positionsRepos.findAllByCategoryId(categoryId);
        if(res.isEmpty()){
            throw new NoContentException("No positions found");
        }
        return res.stream().map(Position::toMinDto).toList();
    }

    public Position getPositionById(Long id){
        return positionsRepos.findById(id).orElse(null);
    }

    public Position addPosition(PositionCreateDto positionCreateDto, MultipartFile image) {
        try{
            Position position;
            if(positionCreateDto.getId() != 0){
                position = positionsRepos.findById(positionCreateDto.getId()).orElseThrow(() -> new PositionCreateException("Position id not found"));
                position.getIngredients().clear();
            }
            else {
                position = new Position();
            }

            Category category = this.categoriesRepos.findById(positionCreateDto.getCategoryId()).orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            position.setName(positionCreateDto.getName());
            position.setDescription(positionCreateDto.getDescription());
            position.setWeight(positionCreateDto.getWeight());
            position.setPrice(positionCreateDto.getPrice());
            position.setMinimumAmount(positionCreateDto.getMinimumAmount() == null ? 10 : positionCreateDto.getMinimumAmount());
            position.setCategory(category);
            position.setAccessible(positionCreateDto.getIsAccessible() == null ? true : positionCreateDto.getIsAccessible());

            if(image != null && !image.isEmpty()){
                position.setImgUrl(uploadImage(image));
            }
            else if(positionCreateDto.getId() == 0){
                position.setImgUrl(positionCreateDto.getImgUrl());
            }

            long id = this.positionsRepos.save(position).getId();
            position = this.positionsRepos.findById(id).orElseThrow(() -> new PositionCreateException("Can't save position"));
            List<IngredientAmount> ingsAmount = new ArrayList<>();
            for (IngAmountRequestDto x : positionCreateDto.getIngredients()){
                IngredientAmount ingredientAmount = new IngredientAmount();
                ingredientAmount.setAmount(x.getAmount());
                ingredientAmount.setUnit(this.unitRepos.findByUnitName(x.getUnit()));
                ingredientAmount.setIngredient(this.ingredientsRepos.findById(x.getIngredient().getId()).orElse(null));
                ingredientAmount.setPosition(this.positionsRepos.findById(id).orElse(null));
                ingsAmount.add(ingredientAmount);
            }
            position.getIngredients().addAll(ingsAmount);

            return this.positionsRepos.save(position);
        } catch (PositionCreateException e){
            throw e;
        } catch (Exception e){
            throw new PositionCreateException("Cannot save position");
        }
    }

    public Long removePosition(Long id) {
        Position position = this.positionsRepos.findById(id).orElseThrow(() -> new NoContentException("Position not found"));
        try{
            this.positionsRepos.delete(position);
            return position.getId();
        }
        catch (Exception e){
            throw new PositionDeleteException("Cannot delete position");
        }
    }

    private String uploadImage(MultipartFile image) {
        if (this.positionImagesContainerClient == null) {
            throw new PositionCreateException("Azure Blob connection is not configured");
        }

        try {
            this.positionImagesContainerClient.createIfNotExists();
            String extension = getFileExtension(image.getOriginalFilename());
            String blobName = UUID.randomUUID() + (extension.isBlank() ? "" : "." + extension);
            BlobClient blobClient = this.positionImagesContainerClient.getBlobClient(blobName);
            String contentType = image.getContentType();

            blobClient.upload(BinaryData.fromBytes(image.getBytes()), true);
            if (contentType != null && !contentType.isBlank()) {
                blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(contentType));
            }

            return this.positionImagesContainerUrl + "/" + blobName;
        } catch (Exception e) {
            throw new PositionCreateException("Cannot upload image");
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
