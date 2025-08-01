package com.bezkoder.springjwt.payload.request.Ingredients;

import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CreateDto {
    private String name;
}
