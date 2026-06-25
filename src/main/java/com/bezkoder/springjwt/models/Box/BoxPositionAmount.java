package com.bezkoder.springjwt.models.Box;

import com.bezkoder.springjwt.models.Position.Position;
import com.bezkoder.springjwt.payload.response.Boxes.BoxPositionResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "box_position_amount")
public class BoxPositionAmount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Box box;

    @ManyToOne(fetch = FetchType.EAGER)
    private Position position;

    @Column(nullable = false)
    private int amount;

    public BoxPositionResponse toResponse() {
        return BoxPositionResponse.builder()
                .id(id)
                .position(position.toResponseDto())
                .amount(amount)
                .build();
    }
}
