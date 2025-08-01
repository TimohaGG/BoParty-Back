package com.bezkoder.springjwt.payload.request.Ingredients;

import com.bezkoder.springjwt.payload.response.Positions.CategoryResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngRequestDto {
    private Long id;
    private String name;
    private CategoryResponseDto category;

}
