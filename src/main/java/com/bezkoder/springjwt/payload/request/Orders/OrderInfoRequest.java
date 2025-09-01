package com.bezkoder.springjwt.payload.request.Orders;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoRequest {
    private String title;
    private String description;
    private int price;
}
