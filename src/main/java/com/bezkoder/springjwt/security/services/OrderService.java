package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Menu.Menu;
import com.bezkoder.springjwt.models.Order.OrderData;
import com.bezkoder.springjwt.payload.request.Order.OrderCreateReq;
import com.bezkoder.springjwt.payload.response.Orders.OrderResp;
import com.bezkoder.springjwt.repository.IOrderRepos;
import com.bezkoder.springjwt.security.Exceptions.OrderCreateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final IOrderRepos orderRepos;
    private final MenuService menuService;
    @Autowired
    public OrderService(IOrderRepos orderRepos,  MenuService menuService) {
        this.orderRepos = orderRepos;
        this.menuService = menuService;
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
}
