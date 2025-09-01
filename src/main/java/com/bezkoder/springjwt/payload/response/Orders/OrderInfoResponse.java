package com.bezkoder.springjwt.payload.response.Orders;

import com.bezkoder.springjwt.models.Order.Orders;
import jakarta.annotation.Nullable;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderInfoResponse {
    private Long id;
    private String title;
    private String description;
    private int price;
}
