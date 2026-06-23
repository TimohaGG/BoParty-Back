package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Position.Category;
import com.bezkoder.springjwt.payload.request.Position.PositionCategoryCreateReq;
import com.bezkoder.springjwt.payload.response.Positions.CategoryResponseDto;
import com.bezkoder.springjwt.repository.CategoriesRepos;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import com.bezkoder.springjwt.security.services.CategoriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/positions/categories")
public class PositionsCategoriesController {

    private final CategoriesService categoriesService;

    public PositionsCategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(@PathVariable long userId){
        try{
            return ResponseEntity.ok(categoriesService.getAll(userId));

        }catch(Exception e){
            throw new NoContentException("There are no categories");
        }

    }

    @PostMapping("/add")
    public ResponseEntity<CategoryResponseDto> addCategory(@RequestBody PositionCategoryCreateReq req){
        return ResponseEntity.ok(this.categoriesService.addCategory(req).toResponseDto());
    }



}
