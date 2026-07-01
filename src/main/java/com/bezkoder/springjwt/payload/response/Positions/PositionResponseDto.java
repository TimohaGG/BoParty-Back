package com.bezkoder.springjwt.payload.response.Positions;

import com.bezkoder.springjwt.payload.request.Ingredients.IngAmountRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PositionResponseDto {
    private Long id;
    private String name;
    private String description;
    private double weight;
    private double price;
    private int minimumAmount;
    private String imgUrl;
    private Boolean accessible;
    private CategoryResponseDto category;
    private List<IngAmountRequestDto> ingredients;
}
