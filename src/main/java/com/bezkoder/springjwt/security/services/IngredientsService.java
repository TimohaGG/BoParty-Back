package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Position.Ingredient;
import com.bezkoder.springjwt.models.Position.IngredientCategory;
import com.bezkoder.springjwt.payload.request.Ingredients.ChangeIngredientCategoryDto;
import com.bezkoder.springjwt.payload.request.Ingredients.CreateDto;
import com.bezkoder.springjwt.payload.request.Ingredients.CreateIngDto;
import com.bezkoder.springjwt.payload.request.Ingredients.RenameDto;
import com.bezkoder.springjwt.payload.response.Ingredients.IngredientResponse;
import com.bezkoder.springjwt.payload.response.Positions.CategoryResponseDto;
import com.bezkoder.springjwt.repository.IIngCategoryRepos;
import com.bezkoder.springjwt.repository.IIngredientsRepos;
import com.bezkoder.springjwt.repository.UserRepos;
import com.bezkoder.springjwt.security.Exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientsService {
    private final IIngredientsRepos igsRepos;
    private final IIngCategoryRepos ingCategoryRepos;
    private final UserRepos userRepos;
    @Autowired
    public IngredientsService(IIngredientsRepos igsRepos, IIngCategoryRepos ingCategoryRepos, UserRepos userRepos) {
        this.igsRepos = igsRepos;
        this.ingCategoryRepos = ingCategoryRepos;
        this.userRepos = userRepos;
    }

    public List<IngredientResponse> getAll(){
        List<IngredientResponse> res = igsRepos.findAll().stream().map(Ingredient::toIngredientDto).toList();
        if(res.isEmpty()){
            throw new NoContentException("No ingredients found");
        }
        return res;
    }

    public List<CategoryResponseDto> getAllCategories() {
        List<CategoryResponseDto> res = ingCategoryRepos.findAll().stream().map(IngredientCategory::toCategoryDto).toList();
        if(res.isEmpty()){
            throw new NoContentException("No ingredients categories found");
        }
        return res;
    }

    public String renameCategory(RenameDto dto) {
        IngredientCategory category = this.ingCategoryRepos.findById(dto.id)
                .orElseThrow(()->new CategoryNotFoundException("Category not found"));
        category.setName(dto.name);
        this.ingCategoryRepos.save(category);
        return category.getName();
    }

    public String renameIngredient(RenameDto dto) {
        Ingredient ing = this.igsRepos.findById(dto.id)
                .orElseThrow(()->new CategoryNotFoundException("Ingredient not found"));
        ing.setName(dto.name);
        this.igsRepos.save(ing);
        return ing.getName();
    }

    public Ingredient changeIngredientCategory(ChangeIngredientCategoryDto dto) {
        Ingredient ingredient = this.igsRepos.findById(dto.getId())
                .orElseThrow(() -> new CategoryNotFoundException("Ingredient not found"));
        IngredientCategory category = this.ingCategoryRepos.findById(dto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        ingredient.setIngCategory(category);
        return this.igsRepos.save(ingredient);
    }

    public IngredientCategory addCategory(CreateDto dto) {

        try{
            IngredientCategory category = IngredientCategory.builder()
                    .name(dto.getName())
                    .build();
            this.ingCategoryRepos.save(category);
            return category;
        }catch(Exception e){
            throw new CategoryCreateException("Category wasn't created");
        }

    }

    public Ingredient addIngredient(CreateIngDto createDto) {
        Ingredient ing = Ingredient.builder()
                .name(createDto.getName())
                .ingCategory(this.ingCategoryRepos.findById(createDto.categoryId).orElseThrow(()->new CategoryNotFoundException("Category not found")))
                .user(this.userRepos.findById(createDto.userId).orElseThrow(()->new UserNotFoundException("User not found")))
                .build();

        try {
            return this.igsRepos.save(ing);
        }catch (Exception ex){
            throw new IngredientCreationException("Ingredient wasn't created");
        }
    }

    public Boolean removeIngCategory(long categoryId) {
        try{
            this.ingCategoryRepos.deleteById(categoryId);
            return true;
        }
        catch (Exception ex){
            throw new CategoryDeleteException("Category delete error");
        }

    }
    public Boolean removeIngredient(long ingredientId) {
        try{
            this.igsRepos.deleteById(ingredientId);
            return true;
        }
        catch (Exception ex){
            throw new IngredientDeleteException("Can't delete ingredient!");
        }
    }

}
