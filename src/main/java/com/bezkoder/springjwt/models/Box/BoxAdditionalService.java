package com.bezkoder.springjwt.models.Box;

import com.bezkoder.springjwt.payload.request.Boxes.BoxAdditionalServiceRequest;
import com.bezkoder.springjwt.payload.response.Boxes.BoxAdditionalServiceResponse;
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
@Table(name = "box_additional_service")
public class BoxAdditionalService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Box box;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private int price;

    public static BoxAdditionalService fromRequest(BoxAdditionalServiceRequest req, Box box) {
        return BoxAdditionalService.builder()
                .box(box)
                .text(req.getText().trim())
                .price(req.getPrice())
                .build();
    }

    public BoxAdditionalServiceResponse toResponse() {
        return BoxAdditionalServiceResponse.builder()
                .id(id)
                .text(text)
                .price(price)
                .build();
    }
}
