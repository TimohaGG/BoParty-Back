package com.bezkoder.springjwt.payload.response.Menu;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MenuInfoResponse {
    private Long id;
    private String title;
    private String description;
    private int price;
}
