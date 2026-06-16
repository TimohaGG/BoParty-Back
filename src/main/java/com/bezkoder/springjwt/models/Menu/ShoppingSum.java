package com.bezkoder.springjwt.models.Menu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingSum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Builder.Default
    private LocalDate date = LocalDate.now();

    private int sum;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Expences expences;

    @PrePersist
    private void prePersist() {
        if (this.date == null) {
            this.date = LocalDate.now();
        }
    }
}
