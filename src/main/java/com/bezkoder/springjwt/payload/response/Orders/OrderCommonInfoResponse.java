package com.bezkoder.springjwt.payload.response.Orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OrderCommonInfoResponse {
    private Long id;
    private String title;
    private String description;
    private int price;
}
