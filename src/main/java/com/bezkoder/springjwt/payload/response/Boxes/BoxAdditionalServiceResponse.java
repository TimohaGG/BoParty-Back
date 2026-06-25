package com.bezkoder.springjwt.payload.response.Boxes;

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
public class BoxAdditionalServiceResponse {
    private Long id;
    private String text;
    private int price;
}
