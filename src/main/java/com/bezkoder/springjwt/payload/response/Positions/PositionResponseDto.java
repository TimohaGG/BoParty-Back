package com.bezkoder.springjwt.payload.response.Positions;

import com.bezkoder.springjwt.models.Position.Category;
import com.bezkoder.springjwt.models.Position.IngAmountDTO;
import com.bezkoder.springjwt.payload.request.Ingredients.IngAmountRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private double weight;
    private double price;
    private String image;
    private CategoryResponseDto category;
    private List<IngAmountRequestDto> ingredients;
}
