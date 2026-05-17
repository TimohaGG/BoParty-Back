package com.bezkoder.springjwt.payload.response.Menu;

import com.bezkoder.springjwt.models.Menu.Menu;
import com.bezkoder.springjwt.models.Menu.ShoppingListItem;
import com.bezkoder.springjwt.payload.response.Positions.PositionAmountResponse;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShoppingListResp {

    private Long id;
    private List<ShoppingListItemResp> items;
    private boolean needsUpdate;
    private List<PositionAmountResponse> positions;
}
