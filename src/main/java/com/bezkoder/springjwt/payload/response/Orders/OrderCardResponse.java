package com.bezkoder.springjwt.payload.response.Orders;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderCardResponse {
    private long id;
    private String date;
    private long sum;
}
