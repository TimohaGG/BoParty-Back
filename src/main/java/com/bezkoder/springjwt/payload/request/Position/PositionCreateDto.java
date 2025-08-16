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

    private long categoryId;

    private IngAmountRequestDto[] ingredients;

//    private byte[] image;



}
