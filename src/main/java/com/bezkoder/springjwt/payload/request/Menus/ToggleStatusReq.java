package com.bezkoder.springjwt.payload.request.Menus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToggleStatusReq {
    private long id;
    private boolean status;
}
