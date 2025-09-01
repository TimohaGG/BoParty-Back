package com.bezkoder.springjwt.payload.response.Orders;

import com.bezkoder.springjwt.models.Order.OrderAdditionalInfo;
import com.bezkoder.springjwt.models.Order.Status;
import com.bezkoder.springjwt.models.Position.PositionAmount;
import com.bezkoder.springjwt.payload.response.Positions.PositionAmountResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private LocalDateTime date;
    private String client;
    private int guestsAmount;
    private int duration;
    private String format;
    private String phone;

    private double totalPrice;

    private List<PositionAmountResponse> positions;

    private List<OrderInfoResponse> additionalInfo;

}
