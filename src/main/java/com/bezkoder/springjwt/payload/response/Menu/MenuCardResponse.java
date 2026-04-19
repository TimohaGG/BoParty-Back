package com.bezkoder.springjwt.payload.response.Menu;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MenuCardResponse {
    private long id;
    private String date;
    private long sum;
    private String client;
}
