package com.bezkoder.springjwt.payload.request.Orders;

import com.bezkoder.springjwt.payload.request.Position.PosAmountRequest;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequest {
    private String date;
    private String client;
    private int guestsAmount;
    private int duration;
    private String format;
    private String phoneNumber;
    private List<PosAmountRequest> positions;
    private List<OrderInfoRequest> additionalInfo;
}
