package com.bezkoder.springjwt.payload.response.Boxes;

import com.bezkoder.springjwt.payload.response.Positions.PositionResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoxPositionResponse {
    private Long id;
    private PositionResponseDto position;
    private int amount;
}
