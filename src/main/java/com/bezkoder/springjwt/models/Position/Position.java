package com.bezkoder.springjwt.models.Position;

import com.bezkoder.springjwt.payload.response.Positions.PositionMinDto;
import com.bezkoder.springjwt.payload.response.Positions.PositionResponseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private double weight;
    @Column(nullable = false)
    private double price;

    @Builder.Default
    @ColumnDefault("10")
    private int minimumAmount = 10;

    @Builder.Default
    @ColumnDefault("true")
    private boolean isAccessible = true;


    @Lob
    @Column(name = "image", columnDefinition = "LONGBLOB", nullable = true)
    private byte[] image;

    @JsonIgnore
    @Transient
    private MultipartFile multipartFile;

    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;

    @JsonIgnore
    @OneToMany(mappedBy = "position", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IngredientAmount> ingredients = new  ArrayList<>();


    public Position(String name, Double weight, double price, MultipartFile multipartFile, Category category, List<IngredientAmount> ingredients) {
        this.name = name;
        this.weight = weight;
        this.price = price;
        this.multipartFile = multipartFile;
        this.category = category;
        this.ingredients = ingredients;
    }

    public String getImageBase64() {
        if(image == null) {
            return "";
        }
        return Base64.getEncoder().encodeToString(image);
    }

    public void setIngredients(List<IngredientAmount> ingredients) {
        this.ingredients = new ArrayList<>();
        this.ingredients = ingredients;
    }

    public void addIngredientAmount(IngredientAmount ingredientAmount) {
        if(this.ingredients == null) {
            this.ingredients = new ArrayList<>();
        }
        this.ingredients.add(ingredientAmount);
    }

    public PositionResponseDto toResponseDto() {
        return PositionResponseDto.builder()
                .id(id)
                .name(name)
                .weight(weight)
                .price(price)
                .minimumAmount(minimumAmount)
                .image(image==null ? "": Base64.getEncoder().encodeToString(image))
                .isAccessible(isAccessible)
                .category(category.toResponseDto())
                .ingredients(ingredients.stream().map(ing->ing.toDTO()).toList())
                .build();
    }

    public PositionMinDto toMinDto() {
        return PositionMinDto.builder()
                .id(id)
                .name(name)
                .weight(weight)
                .price(price)
                .minimumAmount(minimumAmount)
                .image(image==null ? "": Base64.getEncoder().encodeToString(image))
                .isAccessible(isAccessible)
                .category(category.toResponseDto())
                .build();
    }
}
