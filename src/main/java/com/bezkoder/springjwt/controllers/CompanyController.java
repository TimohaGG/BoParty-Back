package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.request.Company.CompanyCreateRequest;
import com.bezkoder.springjwt.payload.request.Company.LinkUserCompanyRequest;
import com.bezkoder.springjwt.payload.response.Company.CompanyResponseDto;
import com.bezkoder.springjwt.payload.response.User.UserCompanyResponseDto;
import com.bezkoder.springjwt.security.services.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/companies")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<List<CompanyResponseDto>> getAll() {
        return ResponseEntity.ok(companyService.getAll());
    }

    @GetMapping("/public")
    public ResponseEntity<CompanyResponseDto> getDefaultForPublic() {
        return ResponseEntity.ok(companyService.getDefaultForPublic().toResponseDto());
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<List<UserCompanyResponseDto>> getUsers() {
        return ResponseEntity.ok(companyService.getUsers());
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<CompanyResponseDto> create(@RequestBody CompanyCreateRequest request) {
        return ResponseEntity.ok(companyService.create(request).toResponseDto());
    }

    @PostMapping("/link-user")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Long> linkUser(@RequestBody LinkUserCompanyRequest request) {
        User user = companyService.linkUser(request);
        return ResponseEntity.ok(user.getId());
    }

    @PostMapping("/{companyId}/public")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<CompanyResponseDto> setDefaultForPublic(@org.springframework.web.bind.annotation.PathVariable Long companyId) {
        return ResponseEntity.ok(companyService.setDefaultForPublic(companyId).toResponseDto());
    }
}
