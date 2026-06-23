package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Menu.Menu;
import com.bezkoder.springjwt.models.Order.OrderData;
import com.bezkoder.springjwt.models.Position.Position;
import com.bezkoder.springjwt.payload.request.Order.OrderCreateReq;
import com.bezkoder.springjwt.payload.response.Orders.OrderResp;
import com.bezkoder.springjwt.payload.response.Positions.PositionMinDto;
import com.bezkoder.springjwt.payload.response.Positions.PositionResponseDto;
import com.bezkoder.springjwt.repository.IOrderRepos;
import com.bezkoder.springjwt.repository.PositionsRepos;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import com.bezkoder.springjwt.security.Exceptions.OrderCreateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final IOrderRepos orderRepos;
    private final MenuService menuService;
    private final PositionsRepos positionsRepos;
    @Autowired
    public OrderService(IOrderRepos orderRepos,  MenuService menuService,  PositionsRepos positionsRepos) {
        this.orderRepos = orderRepos;
        this.menuService = menuService;
        this.positionsRepos = positionsRepos;
    }

    public OrderData addOrder(OrderCreateReq orderCreateReq){
        Menu menu = this.menuService.getOrderById(orderCreateReq.getMenuId());
        if(menu == null){
            throw new OrderCreateException("Menu not found");
        }

        OrderData orderData =  OrderData.builder()
                .menu(menu)
                .shoppingSum(orderCreateReq.getShoppingSum())
                .status(orderCreateReq.getStatus())
                .staffSum(orderCreateReq.getStaffSum())
                .build();

        try{
            return this.orderRepos.save(orderData);
        }catch (Exception e){
            throw new OrderCreateException(e.getMessage());
        }
    }

    public List<OrderResp> getALl() {
        return this.orderRepos.findAll().stream().map(OrderData::toOrderResp).toList();
    }

    public List<PositionMinDto> getAccessibleByCategory(Long categoryId) {
        List<Position> res = this.positionsRepos.findAccessibleByCategoryId(categoryId);
        if(res.isEmpty()){
            throw new NoContentException("No positions found!");
        }
        return res.stream().map(Position::toMinDto  ).toList();
    }
}
