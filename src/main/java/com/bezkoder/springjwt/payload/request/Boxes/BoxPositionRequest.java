package com.bezkoder.springjwt.payload.request.Boxes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoxPositionRequest {
    private Long positionId;
    private Integer amount;
}
