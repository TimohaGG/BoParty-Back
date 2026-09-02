package com.bezkoder.springjwt.controllers;

import ch.qos.logback.core.model.Model;
import com.bezkoder.springjwt.models.Position.Position;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.request.Position.PositionAccessibilityRequestDto;
import com.bezkoder.springjwt.payload.request.Position.PositionCreateDto;
import com.bezkoder.springjwt.payload.response.Positions.PositionMinDto;
import com.bezkoder.springjwt.payload.response.Positions.PositionResponseDto;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import com.bezkoder.springjwt.security.Exceptions.PositionCreateException;
import com.bezkoder.springjwt.security.services.PositionsService;
import com.bezkoder.springjwt.security.services.UserDetailsServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Controller
@RequestMapping("/api/positions")
public class PositionsController {

    private final PositionsService positionsService;
    private final UserDetailsServiceImpl userDetailsService;

    public PositionsController(PositionsService positionsService, UserDetailsServiceImpl userDetailsService) {
        this.positionsService = positionsService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<PositionMinDto>> getPositions(Model model) {
        User current = this.userDetailsService.getCurrentUser();
        List<PositionMinDto> res = positionsService.getAllPositions(current.getId()).stream().map(Position::toMinDto).toList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/archive")
    public ResponseEntity<List<PositionMinDto>> getArchivedPositions() {
        User current = this.userDetailsService.getCurrentUser();
        List<PositionMinDto> res = positionsService.getArchivedPositions(current.getId()).stream().map(Position::toMinDto).toList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<PositionResponseDto> getPositionById(@PathVariable Long id) {
        Position position = positionsService.getPositionById(id);
        if (position == null) {
            throw new NoContentException("Position not found");
        }
        return ResponseEntity.ok(position.toResponseDto());
    }

    @GetMapping("/get/category/{categoryId}")
    public ResponseEntity<List<PositionMinDto>> getPositionsByCategory(@PathVariable Long categoryId) {
        List<PositionMinDto> res = positionsService.getAllPositionsByCategoryId(categoryId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<PositionResponseDto> addPosition(@RequestParam(required = false) MultipartFile image, @RequestParam String position) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PositionCreateDto positionCreateDto = objectMapper.readValue(position, PositionCreateDto.class);
            Position res = this.positionsService.addPosition(positionCreateDto, image);
            return new ResponseEntity<>(res.toResponseDto(), HttpStatus.OK);
        } catch (JsonProcessingException e) {
            throw new PositionCreateException("Cannot parse position");
        }
    }

    @PostMapping("/accessibility")
    public ResponseEntity<PositionResponseDto> updatePositionAccessibility(@RequestBody PositionAccessibilityRequestDto request) {
        Position res = this.positionsService.updateAccessibility(request.getId(), request.isAccessible());
        return ResponseEntity.ok(res.toResponseDto());
    }

    @PostMapping("/{id}/cooking-image")
    public ResponseEntity<PositionResponseDto> updateCookingImage(@PathVariable Long id, @RequestParam MultipartFile image) {
        Position res = this.positionsService.updateCookingImage(id, image);
        return ResponseEntity.ok(res.toResponseDto());
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<PositionResponseDto> archivePosition(@PathVariable Long id) {
        Position res = this.positionsService.updateArchiveStatus(id, true);
        return ResponseEntity.ok(res.toResponseDto());
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<PositionResponseDto> restorePosition(@PathVariable Long id) {
        Position res = this.positionsService.updateArchiveStatus(id, false);
        return ResponseEntity.ok(res.toResponseDto());
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Long> removePosition(@RequestParam Long id) {
        return ResponseEntity.ok(this.positionsService.removePosition(id));
    }

    @GetMapping("/generate/full-menu")
    public ResponseEntity<byte[]> generateFullMenuPdf() {
        User current = this.userDetailsService.getCurrentUser();
        ByteArrayOutputStream out = this.positionsService.generateFullMenuPdf(current.getId());
        byte[] pdfBytes = out.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition
                .attachment()
                .filename("full-menu.pdf")
                .build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
