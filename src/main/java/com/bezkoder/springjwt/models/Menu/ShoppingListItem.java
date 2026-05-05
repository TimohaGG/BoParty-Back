package com.bezkoder.springjwt.models.Menu;

import com.bezkoder.springjwt.models.Position.Ingredient;
import com.bezkoder.springjwt.models.Position.Units;
import com.bezkoder.springjwt.payload.response.Menu.ShoppingListItemResp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
public class    ShoppingListItem{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Ingredient ingredient;

    private double amount;

    @ManyToOne(fetch = FetchType.EAGER)
    private Units unit;

    @ColumnDefault("false")
    private boolean isBought;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private ShoppingList shoppingList;

    @Column(nullable = true)
    private String comment;

    public ShoppingListItem(Ingredient ingredient, ShoppingList shoppingList, double amount, Units unit) {
        this.ingredient = ingredient;
        this.shoppingList = shoppingList;
        this.amount = amount;
        this.unit = unit;
    }

    public ShoppingListItem() {

    }

    public long getInsideIngredientId(){
        return ingredient.getId();
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }

    public static ShoppingListItemResp  toRespDto(ShoppingListItem item){
        return ShoppingListItemResp.builder()
                .id(item.getId())
                .comment(item.getComment())
                .amount(item.getAmount())
                .isBought(item.isBought())
                .ingredient(item.ingredient.toIngredientDto())
                .unitName(item.getUnit().getUnitName())
                .build();
    }

//    @Override
//    public boolean equals(Object obj) {
//        if(obj instanceof ShoppingListItem){
//            return ingredient.getId().equals(((ShoppingListItem) obj).ingredient.getId())
//                    && unit.getId().equals(((ShoppingListItem) obj).unit.getId());
//        }
//        return false;
//    }
//
}
