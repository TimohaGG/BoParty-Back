package com.bezkoder.springjwt.payload.request.Ingredients;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeIngredientCategoryDto {
    private long id;
    private long categoryId;
}
