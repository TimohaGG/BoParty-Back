package com.bezkoder.springjwt.models.Order;

import com.bezkoder.springjwt.payload.response.Orders.OrderCommonInfoResponse;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonOrderInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private int price;

    public OrderCommonInfoResponse toResponse(){
        return OrderCommonInfoResponse.builder()
                .id(id)
                .title(title)
                .description(description)
                .price(price)
                .build();
    }
}
