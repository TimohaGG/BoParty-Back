package com.bezkoder.springjwt.payload.response.Boxes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoxResponse {
    private Long id;
    private String name;
    private String description;
    private double totalPrice;

    private List<BoxPositionResponse> positions = new ArrayList<>();
    private List<BoxAdditionalServiceResponse> additionalServices = new ArrayList<>();
}
