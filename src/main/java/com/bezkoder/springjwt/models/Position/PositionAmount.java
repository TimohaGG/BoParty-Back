package com.bezkoder.springjwt.models.Position;

import com.bezkoder.springjwt.models.Menu.Menu;
import com.bezkoder.springjwt.payload.response.Positions.PositionAmountResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PositionAmount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Position position;

    @ManyToOne(fetch = FetchType.EAGER)
    private Menu order;
    private int amount;

    @Column(nullable = true)
    private String title;

    public static PositionAmount copyPositionAmount(PositionAmount old, Menu order){
        PositionAmount positionAmount = new PositionAmount();
        positionAmount.position = old.position;
        positionAmount.order = order;
        positionAmount.amount = old.amount;
        return positionAmount;

    }

    public PositionAmount() {

    }

    public PositionAmount(Position position, Menu order, int amount) {
        this.position = position;
        this.order = order;
        this.amount = amount;
    }
    public PositionAmount(Position position, int amount) {
        this.position = position;
        this.amount = amount;
    }



    public long getPositionId(){
        return position.getId();
    }

    public void addAmount(int amount){
        this.amount += amount;
    }



    public String getPosName() {
        return position.getName();
    }

    public void removeId(){
        id = null;
    }

    public PositionAmountResponse toDto(){
        return PositionAmountResponse.builder()
                .position(position.toResponseDto())
                .amount(amount)
                .title(title)
                .build();
    }

}
