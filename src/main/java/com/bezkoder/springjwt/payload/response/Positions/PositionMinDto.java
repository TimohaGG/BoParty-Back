package com.bezkoder.springjwt.payload.response.Positions;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PositionMinDto {
    private Long id;
    private String name;
    private String description;
    private double weight;
    private double price;
    private int minimumAmount;
    private String imgUrl;
    private String cookingImgUrl;
    private Boolean accessible;
    private Boolean archived;
    private CategoryResponseDto category;
}
