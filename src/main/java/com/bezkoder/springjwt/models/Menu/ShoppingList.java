package com.bezkoder.springjwt.models.Menu;

import com.bezkoder.springjwt.payload.response.Menu.ShoppingListResp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter

public class ShoppingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    private Menu order;

    public ShoppingList() {
    }

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ShoppingListItem> items;


    private boolean needsUpdate = false;

    public void addItem(ShoppingListItem item) {
        if(this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }

    public void removeItem(ShoppingListItem item) {
        if(this.items != null) {
            this.items.remove(item);
        }
        item.setShoppingList(null);
    }

    public void clearItems() {
        if(this.items != null) {
            this.items.clear();
        }

    }


    public void clearOldItems(List<ShoppingListItem> newIngs) {
        if(this.items != null) {


            List<ShoppingListItem> result = newIngs.stream().peek(
                    x->{
                        ShoppingListItem item = this.items.stream().filter(y-> Objects.equals(y.getIngredient().getId(), x.getIngredient().getId())).findFirst().orElse(null);
                        if(item != null) {
                            x.setBought(item.isBought());
                        }
                    }
            ).toList();
            this.items.clear();
            this.items.addAll(result);

        }

    }

    public static ShoppingListResp toRespDto(ShoppingList item){
        return ShoppingListResp.builder()
                .id(item.getId())
                .needsUpdate(item.isNeedsUpdate())
                .items(item.items.stream().map(ShoppingListItem::toRespDto).toList())
                .build();
    }


}
