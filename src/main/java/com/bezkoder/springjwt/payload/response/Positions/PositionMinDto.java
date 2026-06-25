package com.bezkoder.springjwt.payload.response.Positions;

import com.bezkoder.springjwt.payload.request.Ingredients.IngAmountRequestDto;
import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PositionMinDto {
    private Long id;
    private String name;
    private double weight;
    private double price;
    private int minimumAmount;
    private String image;
    private boolean isAccessible;
    private CategoryResponseDto category;
    private List<IngAmountRequestDto> ingredients;
}
