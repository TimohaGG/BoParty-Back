package com.bezkoder.springjwt.payload.response.Menu;

import com.bezkoder.springjwt.models.Menu.ExpencesWaiter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpencesWaiterResponse {
    private Long id;
    private Long waiterId;
    private String name;
    private int price;

    public static ExpencesWaiterResponse from(ExpencesWaiter waiter) {
        return ExpencesWaiterResponse.builder()
                .id(waiter.getId())
                .waiterId(waiter.getWaiter() == null ? null : waiter.getWaiter().getId())
                .name(waiter.getWaiter() == null ? null : waiter.getWaiter().getName())
                .price(waiter.getPrice())
                .build();
    }
}
