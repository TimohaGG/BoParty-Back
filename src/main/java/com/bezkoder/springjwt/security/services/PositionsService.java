package com.bezkoder.springjwt.security.services;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.bezkoder.springjwt.models.PdfConfig;
import com.bezkoder.springjwt.models.Position.*;
import com.bezkoder.springjwt.payload.request.Ingredients.IngAmountRequestDto;
import com.bezkoder.springjwt.payload.request.Position.PositionCreateDto;
import com.bezkoder.springjwt.payload.response.Positions.PositionMinDto;
import com.bezkoder.springjwt.repository.*;
import com.bezkoder.springjwt.security.Exceptions.CategoryNotFoundException;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import com.bezkoder.springjwt.security.Exceptions.PositionCreateException;
import com.bezkoder.springjwt.security.Exceptions.PdfGenerateException;
import com.bezkoder.springjwt.security.Exceptions.PositionDeleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    private final PdfConfig pdfConfig;

    @Autowired
    public PositionsService(PositionsRepos positionsRepos, CategoriesRepos categoriesRepos, PositionAmountRepos positionAmountRepos, IIngredientsRepos ingredientsRepos, IIngAmountRepos iIngAmountRepos, IUnitRepos unitRepos, IIngCategoryRepos iIngCategoryRepos, PdfConfig pdfConfig,
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
        this.pdfConfig = pdfConfig;
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

    public List<Position> getArchivedPositions(long userId){
        List<Position> res = positionsRepos.findArchivedByCategoryUserId(userId);

        if(res.isEmpty()){
            throw new NoContentException("No archived positions found");
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
            position.setAccessible(positionCreateDto.getAccessible() == null ? true : positionCreateDto.getAccessible());
            position.setArchived(positionCreateDto.getArchived() == null ? false : positionCreateDto.getArchived());

            if(image != null && !image.isEmpty()){
                position.setImgUrl(uploadImage(image));
            }
            else if(positionCreateDto.getId() == 0){
                position.setImgUrl(positionCreateDto.getImgUrl());
            }
            if (positionCreateDto.getCookingImgUrl() != null) {
                position.setCookingImgUrl(positionCreateDto.getCookingImgUrl());
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

    public Position updateAccessibility(Long id, boolean accessible) {
        Position position = this.positionsRepos.findById(id).orElseThrow(() -> new NoContentException("Position not found"));
        position.setAccessible(accessible);
        return this.positionsRepos.save(position);
    }

    public Position updateCookingImage(Long id, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new PositionCreateException("Cooking image is required");
        }

        Position position = this.positionsRepos.findById(id).orElseThrow(() -> new NoContentException("Position not found"));
        position.setCookingImgUrl(uploadImage(image));
        return this.positionsRepos.save(position);
    }

    public Position updateArchiveStatus(Long id, boolean archived) {
        Position position = this.positionsRepos.findById(id).orElseThrow(() -> new NoContentException("Position not found"));
        position.setArchived(archived);
        return this.positionsRepos.save(position);
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

    public ByteArrayOutputStream generateFullMenuPdf(Long userId) {
        List<Position> positions = new ArrayList<>(this.getAllPositions(userId));
        positions.sort(Comparator
                .comparingInt((Position position) -> position.getCategory() == null ? Integer.MAX_VALUE : position.getCategory().getSortingOrder())
                .thenComparing(position -> position.getCategory() == null ? "" : position.getCategory().getName(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Position::getName, String.CASE_INSENSITIVE_ORDER));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(generateFullMenuHeader());
            document.add(generateFullMenuTable(positions));
            document.close();
            return out;
        } catch (Exception e) {
            throw new PdfGenerateException("Can't generate full menu PDF!");
        }
    }

    private Element generateFullMenuHeader() {
        PdfPCell titleCell = this.pdfConfig.defaultCellBold("Повне меню", this.pdfConfig.accentTextColor);
        titleCell.setBackgroundColor(this.pdfConfig.accentColor);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        titleCell.setPaddingLeft(12f);

        PdfPTable titleTable = new PdfPTable(1);
        titleTable.setWidthPercentage(100);
        titleTable.setSpacingAfter(18f);
        titleTable.addCell(titleCell);
        return titleTable;
    }

    private Element generateFullMenuTable(List<Position> positions) throws Exception {
        PdfPTable table = new PdfPTable(new float[]{2.2f, 1.55f, 1.55f, 4.8f, 1.2f});
        table.setWidthPercentage(100);
        table.setSpacingAfter(8f);

        addFullMenuHeaderRow(table);

        Map<Long, List<Position>> groupedPositions = new LinkedHashMap<>();
        for (Position position : positions) {
            if (position.getCategory() == null) {
                continue;
            }
            groupedPositions.computeIfAbsent(position.getCategory().getId(), key -> new ArrayList<>()).add(position);
        }

        for (List<Position> categoryPositions : groupedPositions.values()) {
            if (categoryPositions.isEmpty()) {
                continue;
            }

            Category category = categoryPositions.get(0).getCategory();
            PdfPCell categoryCell = this.pdfConfig.defaultCellBold(category.getName(), this.pdfConfig.accentTextColor);
            categoryCell.setColspan(5);
            categoryCell.setBackgroundColor(this.pdfConfig.accentColor);
            table.addCell(categoryCell);

            for (Position position : categoryPositions) {
                table.addCell(buildPositionNameCell(position));
                table.addCell(buildPositionImageCell(position));
                table.addCell(buildPositionCookingImageCell(position));
                table.addCell(buildPositionIngredientsCell(position));
                table.addCell(this.pdfConfig.compactCell(formatPdfAmount(position.getWeight()) + " г", com.itextpdf.text.Font.BOLD));
            }
        }

        return table;
    }

    private void addFullMenuHeaderRow(PdfPTable table) {
        table.addCell(this.pdfConfig.compactCellBold("Позиція", this.pdfConfig.accentTextColor));
        table.addCell(this.pdfConfig.compactCellBold("Фото", this.pdfConfig.accentTextColor));
        table.addCell(this.pdfConfig.compactCellBold("Приготування", this.pdfConfig.accentTextColor));
        table.addCell(this.pdfConfig.compactCellBold("Інгредієнти", this.pdfConfig.accentTextColor));
        table.addCell(this.pdfConfig.compactCellBold("Вага", this.pdfConfig.accentTextColor));

        for (PdfPCell cell : table.getRow(0).getCells()) {
            cell.setBackgroundColor(this.pdfConfig.summaryHEaderColor);
        }
    }

    private PdfPCell buildPositionNameCell(Position position) {
        StringBuilder content = new StringBuilder(position.getName());
        if (position.getDescription() != null && !position.getDescription().isBlank()) {
            content.append("\n").append(position.getDescription());
        }

        PdfPCell cell = this.pdfConfig.smallCell(content.toString());
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private PdfPCell buildPositionIngredientsCell(Position position) {
        String ingredients = position.getIngredients().stream()
                .map(item -> item.getIngredient().getName() + " - " + formatPdfAmount(item.getAmount()) + " " + item.getUnit().getUnitName())
                .reduce((left, right) -> left + "\n" + right)
                .orElse("-");

        PdfPCell cell = this.pdfConfig.smallCell(ingredients);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private PdfPCell buildPositionImageCell(Position position) {
        if (position.getImgUrl() == null || position.getImgUrl().isBlank()) {
            return this.pdfConfig.compactCell("Без фото");
        }

        return buildImageCell(position.getImgUrl());
    }

    private PdfPCell buildPositionCookingImageCell(Position position) {
        if (position.getCookingImgUrl() == null || position.getCookingImgUrl().isBlank()) {
            return this.pdfConfig.compactCell("Без фото");
        }

        return buildImageCell(position.getCookingImgUrl());
    }

    private PdfPCell buildImageCell(String imageUrl) {
        try {
            Image image = Image.getInstance(new URL(imageUrl));
            image.scaleToFit(110, 110);
            PdfPCell cell = this.pdfConfig.getImageCell(image);
            cell.setFixedHeight(118f);
            return cell;
        } catch (Exception e) {
            return this.pdfConfig.compactCell("Без фото");
        }
    }

    private String formatPdfAmount(double amount) {
        if (amount == Math.rint(amount)) {
            return String.valueOf((long) amount);
        }

        return String.format(Locale.US, "%.2f", amount).replaceAll("0+$", "").replaceAll("/.$", "");
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
