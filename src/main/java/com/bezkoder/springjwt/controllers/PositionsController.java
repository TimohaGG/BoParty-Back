package com.bezkoder.springjwt.controllers;

import ch.qos.logback.core.model.Model;
import com.bezkoder.springjwt.models.Position.Position;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.response.Positions.CategoryResponseDto;
import com.bezkoder.springjwt.payload.request.Position.PositionCreateDto;
import com.bezkoder.springjwt.payload.response.Positions.PositionResponseDto;
import com.bezkoder.springjwt.security.Exceptions.PositionCreateException;
import com.bezkoder.springjwt.security.services.PositionsService;
import com.bezkoder.springjwt.security.services.UserDetailsServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @GetMapping("/get/category/{categoryId}")
    public ResponseEntity<List<PositionResponseDto>> getPositionsByCategory(@PathVariable Long categoryId) {
        User current = this.userDetailsService.getCurrentUser();
        List<PositionResponseDto> res = positionsService.getAllPositionsByCategoryId(categoryId).stream().map(Position::toResponseDto).toList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping( "/add")
    public ResponseEntity<PositionResponseDto> addPosition(@RequestParam(required = false) MultipartFile image, @RequestParam String position) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            PositionCreateDto positionCreateDto = objectMapper.readValue(position, PositionCreateDto.class);
            Position res = this.positionsService.addPosition(positionCreateDto, image);
            return new ResponseEntity<>(res.toResponseDto(),HttpStatus.OK);
        }catch (JsonProcessingException e){
            throw new PositionCreateException("Cannot parse position");
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Long> removePosition(@RequestParam Long id) {
        return ResponseEntity.ok(this.positionsService.removePosition(id));

    }



}


