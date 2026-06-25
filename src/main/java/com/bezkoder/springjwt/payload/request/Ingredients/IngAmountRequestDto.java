package com.bezkoder.springjwt.payload.request.Ingredients;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngAmountRequestDto {
    private long id;
    private IngRequestDto ingredient;
    private double  amount;
    private String unit;
}

