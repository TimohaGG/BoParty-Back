package com.bezkoder.springjwt.payload.request.Menus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingListItemReq {
    private Long ingredientId;
    private Long unitId;
    private String unitName;
    private double amount;
    private Long shoppingListId;
}
