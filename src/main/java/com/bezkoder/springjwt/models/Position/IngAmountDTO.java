package com.bezkoder.springjwt.models.Position;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngAmountDTO {
    private Long ingId;
    private Long unitId;
    private double amount;





    public Long getIngId() {
        return ingId;
    }

    public void setIngId(Long ingId) {
        this.ingId = ingId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
