package com.bezkoder.springjwt.payload.response.Menu;

import com.bezkoder.springjwt.models.Menu.ShoppingSum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingSumResponse {
    private Long id;
    private String name;
    private LocalDate date;
    private int sum;

    public static ShoppingSumResponse from(ShoppingSum shoppingSum) {
        return ShoppingSumResponse.builder()
                .id(shoppingSum.getId())
                .name(shoppingSum.getName())
                .date(shoppingSum.getDate())
                .sum(shoppingSum.getSum())
                .build();
    }
}
