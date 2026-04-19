package com.bezkoder.springjwt.payload.request.Menus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MinMenuPage {
    int pageSize;
    int pageIndex;
}
