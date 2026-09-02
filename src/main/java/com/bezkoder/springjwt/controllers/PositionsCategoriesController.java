package com.bezkoder.springjwt.controllers;

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

    @GetMapping("/get")
    public ResponseEntity<List<CategoryResponseDto>> getCurrentCompanyCategories(){
        return ResponseEntity.ok(categoriesService.getAllForCurrentCompany());
    }

    @GetMapping("/public")
    public ResponseEntity<List<CategoryResponseDto>> getPublicCategories(){
        return ResponseEntity.ok(categoriesService.getAllForPublicCompany());
    }

    @PostMapping("/add")
    public ResponseEntity<CategoryResponseDto> addCategory(@RequestBody PositionCategoryCreateReq req){
        return ResponseEntity.ok(this.categoriesService.addCategory(req).toResponseDto());
    }



}
