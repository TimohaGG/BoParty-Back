package com.bezkoder.springjwt.models.Box;

import com.bezkoder.springjwt.payload.response.Boxes.BoxResponse;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "boxes")
public class Box {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private double totalPrice = 0;

    @Builder.Default
    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoxPositionAmount> positions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "box", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoxAdditionalService> additionalServices = new ArrayList<>();

    public void addPosition(BoxPositionAmount item) {
        item.setBox(this);
        this.positions.add(item);
    }

    public void addAdditionalService(BoxAdditionalService item) {
        item.setBox(this);
        this.additionalServices.add(item);
    }

    public BoxResponse toResponse() {
        return BoxResponse.builder()
                .id(id)
                .name(name)
                .description(description)
                .totalPrice(totalPrice)
                .positions(positions.stream().map(BoxPositionAmount::toResponse).toList())
                .additionalServices(additionalServices.stream().map(BoxAdditionalService::toResponse).toList())
                .build();
    }
}
