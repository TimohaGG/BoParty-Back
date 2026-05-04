package com.bezkoder.springjwt.payload.response.Menu;

import com.bezkoder.springjwt.payload.response.Positions.PositionAmountResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuResponse {
    private Long id;
    private LocalDateTime date;
    private String client;
    private int guestsAmount;
    private int duration;
    private String format;
    private String phone;
    private boolean isPayed;

    private double totalPrice;

    private List<PositionAmountResponse> positions;

    private List<MenuInfoResponse> additionalInfo;

    private boolean serving;
    private double taxAmount;

    private boolean govTax;
    private double govTaxAmount;

}
