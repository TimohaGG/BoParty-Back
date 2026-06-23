package com.bezkoder.springjwt.controllers;

import ch.qos.logback.core.model.Model;
import com.bezkoder.springjwt.models.Position.Position;
import com.bezkoder.springjwt.payload.request.Order.OrderCreateReq;
import com.bezkoder.springjwt.payload.response.Orders.OrderResp;
import com.bezkoder.springjwt.payload.response.Positions.PositionMinDto;
import com.bezkoder.springjwt.payload.response.Positions.PositionResponseDto;
import com.bezkoder.springjwt.security.services.MenuService;
import com.bezkoder.springjwt.security.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public  OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/positions/{categoryId}")
    public ResponseEntity<List<PositionMinDto>> getOrders(@PathVariable Long categoryId) {
        return ResponseEntity.ok(this.orderService.getAccessibleByCategory(categoryId));
    }

//    @PostMapping("/api/orderdatas/create")
//    public String createOrder(OrderCreateReq orderCreateReq){
//        this.orderService.addOrder(orderCreateReq);
//        return "redirect:/orderdatas";
//    }
//
//    @GetMapping("/api/orderdatas")
//    public List<OrderResp> getOrders(Model model){
//        return this.orderService.getALl();
//    }
}
