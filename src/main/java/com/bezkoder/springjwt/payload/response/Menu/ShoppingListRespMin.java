package com.bezkoder.springjwt.payload.response.Menu;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListRespMin {
    private Long id;
    private Long orderId;
    private LocalDateTime date;
    private String client;

}
