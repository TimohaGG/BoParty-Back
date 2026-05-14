package com.bezkoder.springjwt.payload.request.Menus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCommentReq {
    private String comment;
    private long shoppingItemId;
}
