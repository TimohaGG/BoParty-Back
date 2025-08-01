package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.payload.response.Positions.CategoryResponseDto;
import com.bezkoder.springjwt.repository.CategoriesRepos;
import com.bezkoder.springjwt.security.services.CategoriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("positions/categories")
public class PositionsCategoriesController {

    private final CategoriesService categoriesService;

    public PositionsCategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(){
        return ResponseEntity.ok(categoriesService.getAll());
    }
}
