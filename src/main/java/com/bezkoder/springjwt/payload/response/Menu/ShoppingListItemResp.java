package com.bezkoder.springjwt.payload.response.Menu;

import com.bezkoder.springjwt.payload.response.Ingredients.IngredientResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingListItemResp {
    Long id;
    IngredientResponse ingredient;
    double amount;
    boolean isBought;
    String comment;
    String unitName;
}
