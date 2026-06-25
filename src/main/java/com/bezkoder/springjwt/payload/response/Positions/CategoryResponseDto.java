package com.bezkoder.springjwt.payload.response.Positions;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CategoryResponseDto {
    private Long id;
    private String name;
    private long userId;
}
