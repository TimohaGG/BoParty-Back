package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Position.IngredientCategory;
import com.bezkoder.springjwt.payload.request.Ingredients.CreateDto;
import com.bezkoder.springjwt.payload.request.Ingredients.ChangeIngredientCategoryDto;
import com.bezkoder.springjwt.payload.request.Ingredients.CreateIngDto;
import com.bezkoder.springjwt.payload.request.Ingredients.RenameDto;
import com.bezkoder.springjwt.payload.response.Ingredients.IngredientResponse;
import com.bezkoder.springjwt.payload.response.Ingredients.RenameResponse;
import com.bezkoder.springjwt.payload.response.Positions.CategoryResponseDto;
import com.bezkoder.springjwt.security.services.IngredientsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/ingredients")
public class IngredientsController {
    private final IngredientsService igsService;
    public IngredientsController(IngredientsService igsService) {
        this.igsService = igsService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<IngredientResponse>> getAll(){
        return ResponseEntity.ok(this.igsService.getAll());
    }


    @PostMapping("/add")
    public ResponseEntity<IngredientResponse> add(@RequestBody CreateIngDto createDto){
        return ResponseEntity.ok(this.igsService.addIngredient(createDto).toIngredientDto());
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Boolean> remove(@RequestParam long id){
        return ResponseEntity.ok(this.igsService.removeIngredient(id));
    }


    @GetMapping("categories/get")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(){
        return ResponseEntity.ok(this.igsService.getAllCategories());
    }

    @PostMapping("categories/rename")
    public ResponseEntity<RenameResponse> renameCategories(@RequestBody RenameDto dto){
        String res = this.igsService.renameCategory(dto);
        return ResponseEntity.ok(RenameResponse.builder().name(res).build());
    }

    @DeleteMapping("/categories/remove")
    public ResponseEntity<Boolean> removeCategories(@RequestParam long categoryId){
        return ResponseEntity.ok(this.igsService.removeIngCategory(categoryId));
    }

    @PostMapping("/rename")
    public ResponseEntity<RenameResponse> renameIngredient(@RequestBody RenameDto dto){
        String res = this.igsService.renameIngredient(dto);
        return ResponseEntity.ok(RenameResponse.builder().name(res).build());
    }

    @PostMapping("/category/change")
    public ResponseEntity<IngredientResponse> changeIngredientCategory(@RequestBody ChangeIngredientCategoryDto dto){
        return ResponseEntity.ok(this.igsService.changeIngredientCategory(dto).toIngredientDto());
    }

    @PostMapping("/categories/add")
    public ResponseEntity<CategoryResponseDto> addIngCategory(@RequestBody CreateDto dto){
        IngredientCategory res = this.igsService.addCategory(dto);
        return ResponseEntity.ok(res.toCategoryDto());
    }
}
