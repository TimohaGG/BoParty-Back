package com.bezkoder.springjwt.payload.request.Position;

import com.bezkoder.springjwt.payload.request.Ingredients.IngAmountRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PositionCreateDto {
    private long id;
    private String name;
    private int weight;
    private int price;
    private Integer minimumAmount = 10;

    private long categoryId;

    private Boolean isAccessible = true;

    private IngAmountRequestDto[] ingredients;

//    private byte[] image;



}
