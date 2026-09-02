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
    private Long companyId;
    private String companyName;
    private int sortingOrder;

    public CategoryResponseDto(Long id, String name, Long companyId, String companyName, int sortingOrder) {
        this.id = id;
        this.name = name;
        this.companyId = companyId;
        this.companyName = companyName;
        this.sortingOrder = sortingOrder;
    }
}
