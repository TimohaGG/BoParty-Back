package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Order.Orders;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.security.Exceptions.UserNotFoundException;
import com.bezkoder.springjwt.security.services.OrdersService;
import com.bezkoder.springjwt.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;

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
    public ResponseEntity<List<Orders>> getAll() throws InterruptedException {

       User current = this.userDetailsService.getCurrentUser();
        if(current==null) {
            throw new UserNotFoundException("Can't find current user");
        }
        List<Orders> res = this.ordersService.getOrdersByUserId(current.getId());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


}
