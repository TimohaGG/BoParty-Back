package com.bezkoder.springjwt.payload.response.Menu;

import com.bezkoder.springjwt.models.Menu.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime date;
    private double totalPrice;
    private String client;
    private boolean isPayed;
    private boolean temporary;
}
