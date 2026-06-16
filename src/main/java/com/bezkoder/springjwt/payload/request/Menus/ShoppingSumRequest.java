package com.bezkoder.springjwt.payload.request.Menus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingSumRequest {
    private Long id;
    private String name;
    private LocalDate date;
    private int sum;
}
