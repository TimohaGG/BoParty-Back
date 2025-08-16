package com.bezkoder.springjwt.payload.request.Ingredients;

import com.bezkoder.springjwt.payload.response.Positions.CategoryResponseDto;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IngRequestDto {
    private Long id;
    private String name;
    private CategoryResponseDto category;

}
