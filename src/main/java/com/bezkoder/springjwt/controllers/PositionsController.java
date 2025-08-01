package com.bezkoder.springjwt.controllers;

import ch.qos.logback.core.model.Model;
import com.bezkoder.springjwt.models.Position.Position;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.response.Positions.PositionCreateDto;
import com.bezkoder.springjwt.payload.response.Positions.PositionResponseDto;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import com.bezkoder.springjwt.security.services.PositionsService;
import com.bezkoder.springjwt.security.services.UserDetailsServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/positions")
public class PositionsController {

    private final PositionsService positionsService;
    private final UserDetailsServiceImpl userDetailsService;

    public PositionsController(PositionsService positionsService, UserDetailsServiceImpl userDetailsService) {

        this.positionsService = positionsService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<PositionResponseDto>> getPositions(Model model) {
        User current = this.userDetailsService.getCurrentUser();
        List<PositionResponseDto> res = positionsService.getAllPositions(current.getId()).stream().map(Position::toResponseDto).toList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping( "/add")
    public ResponseEntity<Position> addPosition(@ModelAttribute PositionCreateDto position) {
        return ResponseEntity.ok(null);
    }



}


