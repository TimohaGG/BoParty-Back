package com.bezkoder.springjwt.payload.response.Menu;

import com.bezkoder.springjwt.models.Menu.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MenuCardResponse {
    private long id;
    private LocalDateTime date;
    private long totalPrice;
    private String client;
    private boolean isPayed;
}
