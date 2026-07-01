package com.bezkoder.springjwt.payload.request.Position;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PositionAccessibilityRequestDto {
    private long id;
    private boolean accessible;
}
