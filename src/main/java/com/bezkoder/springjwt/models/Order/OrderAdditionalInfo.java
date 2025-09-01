package com.bezkoder.springjwt.models.Order;

import com.bezkoder.springjwt.payload.request.Orders.OrderCommonInfoRequest;
import com.bezkoder.springjwt.payload.request.Orders.OrderInfoRequest;
import com.bezkoder.springjwt.payload.response.Orders.OrderInfoResponse;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.Base64;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderAdditionalInfo
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Orders order;

    private String title;
    private String description;

    private int price;


    @Override
    public String toString() {
        return title + "," + description + "," + price;
    }


    public OrderInfoResponse toResponse() {
        return OrderInfoResponse.builder()
                .id(id)
                .title(title)
                .description(description)
                .price(price)
                .build();
    }

    public static OrderAdditionalInfo parse(OrderInfoRequest data){
        return OrderAdditionalInfo.builder()
                .title(data.getTitle())
                .description(data.getDescription())
                .price(data.getPrice())
                .build();
    }

}
