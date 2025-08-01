package com.bezkoder.springjwt.payload.response.Ingredients;

import com.bezkoder.springjwt.payload.response.Positions.CategoryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class IngredientResponse {
    private long id;
    private String name;
    private CategoryResponseDto category;
}
