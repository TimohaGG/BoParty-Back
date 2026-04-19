package com.bezkoder.springjwt.payload.request.Menus;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuInfoRequest {
    private String title;
    private String description;
    private int price;
}
