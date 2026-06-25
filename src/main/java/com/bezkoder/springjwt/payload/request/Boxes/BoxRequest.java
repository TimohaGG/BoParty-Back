package com.bezkoder.springjwt.payload.request.Boxes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoxRequest {
    private Long id;
    private String name;
    private String description;
    private List<BoxPositionRequest> positions = new ArrayList<>();
    private List<BoxAdditionalServiceRequest> additionalServices = new ArrayList<>();
}
