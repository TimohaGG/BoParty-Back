package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.payload.request.Boxes.BoxRequest;
import com.bezkoder.springjwt.payload.response.Boxes.BoxResponse;
import com.bezkoder.springjwt.security.services.BoxService;
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
@RequestMapping("/api/boxes")
public class BoxController {
    private final BoxService boxService;

    public BoxController(BoxService boxService) {
        this.boxService = boxService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<BoxResponse>> getAll() {
        return ResponseEntity.ok(this.boxService.getAll());
    }

    @PostMapping("/create")
    public ResponseEntity<BoxResponse> create(@RequestBody BoxRequest req) {
        return ResponseEntity.ok(this.boxService.create(req));
    }

    @PostMapping("/edit")
    public ResponseEntity<BoxResponse> edit(@RequestBody BoxRequest req) {
        return ResponseEntity.ok(this.boxService.edit(req));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        return ResponseEntity.ok(this.boxService.delete(id));
    }
}
