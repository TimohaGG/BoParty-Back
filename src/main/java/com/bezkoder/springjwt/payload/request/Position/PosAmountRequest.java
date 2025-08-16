package com.bezkoder.springjwt.payload.request.Position;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PosAmountRequest {
    private long posId;
    private int amount;
    private String title;
}
