package com.bezkoder.springjwt.payload.response.Positions;

import com.bezkoder.springjwt.models.Position.Category;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PositionResponseDto {
    private Long id;
    private String name;
    private double weight;
    private double price;
    private byte[] image;
    private CategoryResponseDto category;
}
