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
public class MenuCreateRequest {
    private String date;
    private String client;
    private int guestsAmount;
    private int duration;
    private String format;
    private String phoneNumber;
    private List<PosAmountRequest> positions;
    private List<MenuInfoRequest> additionalInfo;

}
