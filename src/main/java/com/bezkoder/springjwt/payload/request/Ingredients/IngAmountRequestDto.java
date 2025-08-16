package com.bezkoder.springjwt.payload.request.Ingredients;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IngAmountRequestDto {
    private long id;
    private IngRequestDto ingredient;
    private double  amount;
    private String unit;
}

