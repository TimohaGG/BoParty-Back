package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.payload.request.Menus.WaiterRequest;
import com.bezkoder.springjwt.payload.response.Menu.WaiterResponse;
import com.bezkoder.springjwt.security.services.WaiterService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/staff")
public class WaiterController {
    private final WaiterService waiterService;

    public WaiterController(WaiterService waiterService) {
        this.waiterService = waiterService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<WaiterResponse>> getAll() {
        return ResponseEntity.ok(this.waiterService.getAll());
    }

    @PostMapping("/create")
    public ResponseEntity<WaiterResponse> create(@RequestBody WaiterRequest req) {
        return ResponseEntity.ok(this.waiterService.create(req));
    }

    @PostMapping("/edit")
    public ResponseEntity<WaiterResponse> edit(@RequestBody WaiterRequest req) {
        return ResponseEntity.ok(this.waiterService.edit(req));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        return ResponseEntity.ok(this.waiterService.delete(id));
    }
}
