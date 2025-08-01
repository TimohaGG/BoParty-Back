package com.bezkoder.springjwt.models.Position;

import com.bezkoder.springjwt.payload.response.Positions.CategoryResponseDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
public class IngredientCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    public IngredientCategory(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public IngredientCategory() {
    }

    public CategoryResponseDto toCategoryDto() {
        return CategoryResponseDto.builder()
                .id(id)
                .name(name)
                .build();
    }
}
