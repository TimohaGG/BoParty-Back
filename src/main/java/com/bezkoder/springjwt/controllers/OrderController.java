package com.bezkoder.springjwt.controllers;

import ch.qos.logback.core.model.Model;
import com.bezkoder.springjwt.payload.request.Order.OrderCreateReq;
import com.bezkoder.springjwt.payload.response.Orders.OrderResp;
import com.bezkoder.springjwt.security.services.MenuService;
import com.bezkoder.springjwt.security.services.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;

    public  OrderController(OrderService orderService) {
        this.orderService = orderService;

    }
    @PostMapping("/orderdatas/create")
    public String createOrder(OrderCreateReq orderCreateReq){
        this.orderService.addOrder(orderCreateReq);
        return "redirect:/orderdatas";
    }

    @GetMapping("/orderdatas")
    public List<OrderResp> getOrders(Model model){
        return this.orderService.getALl();
    }
}
