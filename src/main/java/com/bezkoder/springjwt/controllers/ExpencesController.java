package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.payload.request.Menus.ExpencesRequest;
import com.bezkoder.springjwt.payload.response.Menu.ExpencesResponse;
import com.bezkoder.springjwt.security.services.ExpencesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/expences")
public class ExpencesController {

    private final ExpencesService expencesService;

    public ExpencesController(ExpencesService expencesService) {
        this.expencesService = expencesService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<ExpencesResponse>> getAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(this.expencesService.getAll(startDate, endDate));
    }

    @PostMapping("/create")
    public ResponseEntity<ExpencesResponse> create(@RequestBody ExpencesRequest req) {
        return ResponseEntity.ok(this.expencesService.create(req));
    }

    @PostMapping("/edit")
    public ResponseEntity<ExpencesResponse> edit(@RequestBody ExpencesRequest req) {
        return ResponseEntity.ok(this.expencesService.edit(req));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        return ResponseEntity.ok(this.expencesService.delete(id));
    }
}
