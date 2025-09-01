package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Order.CommonOrderInfo;
import com.bezkoder.springjwt.models.Order.Orders;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.request.Orders.OrderCommonInfoRequest;
import com.bezkoder.springjwt.payload.request.Orders.OrderCreateRequest;
import com.bezkoder.springjwt.payload.request.Orders.OrderEditRequest;
import com.bezkoder.springjwt.payload.response.Orders.OrderCardResponse;
import com.bezkoder.springjwt.payload.response.Orders.OrderCommonInfoResponse;
import com.bezkoder.springjwt.payload.response.Orders.OrderResponse;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import com.bezkoder.springjwt.security.Exceptions.UserNotFoundException;
import com.bezkoder.springjwt.security.services.OrdersService;
import com.bezkoder.springjwt.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrdersService ordersService;
    private final UserDetailsServiceImpl userDetailsService;
    @Autowired
    public OrderController(OrdersService ordersService, UserDetailsServiceImpl userDetailsService) {
        this.ordersService = ordersService;
        this.userDetailsService = userDetailsService;

    }

    @GetMapping("/get")
    public ResponseEntity<List<OrderResponse>> getAll() throws InterruptedException {

        User current = this.userDetailsService.getCurrentUser();
        if(current==null) {
            throw new UserNotFoundException("Can't find current user");
        }
        List<OrderResponse> res = this.ordersService.getOrdersByUserId(current.getId()).stream().map(Orders::toDto).toList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/get/min")
    public ResponseEntity<List<OrderCardResponse>> getAllMin() {
        User current = this.userDetailsService.getCurrentUser();
        if(current==null) {
            throw new UserNotFoundException("Can't find current user");
        }
        List<OrderCardResponse> res = this.ordersService.getOrdersByUserId(current.getId()).stream().map(Orders::toCardDto).toList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/get/{orderId}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long orderId) throws InterruptedException {
        Orders res = this.ordersService.getOrderById(orderId);
        return new ResponseEntity<>(Orders.toDto(res), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> create(@RequestBody OrderCreateRequest order) {
        Orders res = this.ordersService.createOrder(order);
        return ResponseEntity.ok(Orders.toDto(res));
    }

    @PostMapping("/edit")
    public ResponseEntity<OrderResponse> edit(@RequestBody OrderEditRequest order) {
        Orders res = this.ordersService.editOrder(order);
        return ResponseEntity.ok(Orders.toDto(res));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        long deletedId = this.ordersService.deleteById(id);
        return ResponseEntity.ok(deletedId);
    }


    @GetMapping("/info/common")
    public ResponseEntity<List<CommonOrderInfo>> getCommonInfo(){
        List<CommonOrderInfo> res = this.ordersService.getAllCommonInfo();
        if(res.isEmpty()){
            throw new NoContentException("No common info found");
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping("/info/common/add")
    public ResponseEntity<OrderCommonInfoResponse> add(@RequestBody OrderCommonInfoRequest info) {
        CommonOrderInfo res = this.ordersService.createCommonInfo(info);
        return ResponseEntity.ok(res.toResponse());
    }








}
