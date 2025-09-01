package com.bezkoder.springjwt.models.Order;

import com.bezkoder.springjwt.models.Position.PositionAmount;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.response.Orders.OrderCardResponse;
import com.bezkoder.springjwt.payload.response.Orders.OrderResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Orders(){
        date = LocalDate.now().atStartOfDay();
        client = "";
        guestsAmount = 0;
        duration = 0;
        format = "Бокси";
        phone = "0688714410";
        id = 0L;
        positionsAmount = new ArrayList<>();
        status = Status.CALCULATED;
    }

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime date;


    @Column(nullable = true)
    private String client;
    @Column(nullable = true)
    private int guestsAmount;
    @Column(nullable = true)
    private int duration;
    @Column(nullable = true)
    private String format;
    @ColumnDefault("0688714410")
    private String phone;


    private boolean needsTax = false;

    private double taxPercentage = 0.06D;
    public double getTaxPercentageCalc() {
        double totalPrice = getPrice() + getAdditionalInfo().stream().mapToInt(OrderAdditionalInfo::getPrice).sum();
        return Math.floor(totalPrice - totalPrice * (1-taxPercentage)) ;
    }


    @OneToMany(mappedBy = "order",fetch = FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},orphanRemoval = true)
    private List<PositionAmount> positionsAmount = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "order",fetch = FetchType.EAGER,cascade=CascadeType.ALL,orphanRemoval = true)
    private List<OrderAdditionalInfo> additionalInfo = new  ArrayList<>();


    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private ShoppingList shoppingList;
    @ColumnDefault("false")
    private boolean temporary;

    @ColumnDefault("1")
    private Status status;


    public String getDateFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        if(date!=null){
            return date.format(formatter);
        }
        else{
            return "";
        }

    }

    public int getPrice(){

        return positionsAmount.stream()
                .mapToInt(x -> (int)x.getPosition().getPrice() * x.getAmount())
                .sum();
    }

    public int getTotalPrice(){
        int sum = getPrice() + getAdditionalInfo().stream().mapToInt(OrderAdditionalInfo::getPrice).sum();
        if(needsTax){
            sum += getTaxPercentageCalc();
        }
        return sum;
    }


    public List<PositionAmount> getPositionsAmount() {
        return positionsAmount.stream().sorted(Comparator.comparing(x->x.getPosition().getCategory().getId())).toList();
    }


    public void addPosition(PositionAmount position) {
        positionsAmount.add(position);
    }

    public int getOnOnePerson(){
        if(guestsAmount==0){
            return 0;
        }
        return (int)getPrice() / guestsAmount;
    }


    public void removePosition(PositionAmount positionAmount) {
        this.positionsAmount.remove(positionAmount);
        positionAmount.setOrder(null);
    }

    public static OrderCardResponse toCardDto(Orders order){
        return OrderCardResponse.builder()
                .date(order.getDateFormatted())
                .id(order.getId())
                .sum(order.getTotalPrice())
                .client(order.getClient())
                .build();
    }

    public static OrderResponse toDto(Orders order){
        System.out.println(order.getTotalPrice());
        return OrderResponse.builder()
                .id(order.getId())
                .date(order.getDate())
                .client(order.getClient())
                .guestsAmount(order.getGuestsAmount())
                .duration(order.getDuration())
                .format(order.getFormat())
                .phone(order.getPhone())
                .totalPrice(order.getTotalPrice())
                .positions(order.getPositionsAmount().stream().map(PositionAmount::toDto).toList())
                .additionalInfo(order.getAdditionalInfo().stream().map(OrderAdditionalInfo::toResponse).toList())
                .build();
    }
}
