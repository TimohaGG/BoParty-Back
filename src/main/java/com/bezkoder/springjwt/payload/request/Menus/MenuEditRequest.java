package com.bezkoder.springjwt.payload.request.Menus;

import com.bezkoder.springjwt.payload.request.Position.PosAmountRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MenuEditRequest {
    private long id;
    private String date;
    private String client;
    private int guestsAmount;
    private String duration;
    private String format;
    private String phoneNumber;
    private String deliveryType;
    private String deliveryAddress;
    private String orderType;
    private boolean needsWaiter;
    private List<PosAmountRequest> positions;
    private List<MenuInfoRequest> additionalInfo;
    private boolean serving;
    private double taxAmount;
    private boolean govTax;
    private int govTaxAmount;
}
