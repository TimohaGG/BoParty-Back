package com.bezkoder.springjwt.models.Order;

import com.bezkoder.springjwt.models.Position.PositionAmount;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.response.Orders.OrderCardResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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

    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PositionAmount> positionsAmount;

    @OneToMany(mappedBy = "order",fetch = FetchType.LAZY)
    private List<OrderAdditionalInfo> additionalInfo;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
                .build();
    }
}
