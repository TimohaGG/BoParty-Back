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
    private Long staffId;
    private String name;
    private String type;
    private int price;
    private boolean payed;

    public static ExpencesWaiterResponse from(ExpencesWaiter waiter) {
        return ExpencesWaiterResponse.builder()
                .id(waiter.getId())
                .staffId(waiter.getWaiter() == null ? null : waiter.getWaiter().getId())
                .name(waiter.getWaiter() == null ? null : waiter.getWaiter().getName())
                .type(waiter.getWaiter() == null ? null : waiter.getWaiter().getType())
                .price(waiter.getPrice())
                .payed(waiter.isPayed())
                .build();
    }
}
