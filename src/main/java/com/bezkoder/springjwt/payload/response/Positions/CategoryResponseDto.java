package com.bezkoder.springjwt.payload.response.Positions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor

public class CategoryResponseDto {
    private Long id;
    private String name;
    private long userId;
    private int sortingOrder;

    public CategoryResponseDto(Long id, String name, long userId, int sortingOrder) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.sortingOrder = sortingOrder;
    }
}
