package com.bezkoder.springjwt.payload.response.Menu;

import com.bezkoder.springjwt.models.Menu.Waiter;
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
public class WaiterResponse {
    private Long id;
    private String name;

    public static WaiterResponse from(Waiter waiter) {
        return WaiterResponse.builder()
                .id(waiter.getId())
                .name(waiter.getName())
                .build();
    }
}
