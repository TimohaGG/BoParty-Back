package com.bezkoder.springjwt.payload.response.Positions;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PositionAmountResponse {
    private PositionResponseDto position;
    private long amount;
    private String title;
}
