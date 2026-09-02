package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Company.Company;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.request.Company.CompanyCreateRequest;
import com.bezkoder.springjwt.payload.request.Company.LinkUserCompanyRequest;
import com.bezkoder.springjwt.payload.response.Company.CompanyResponseDto;
import com.bezkoder.springjwt.payload.response.User.UserCompanyResponseDto;
import com.bezkoder.springjwt.repository.CategoriesRepos;
import com.bezkoder.springjwt.repository.CompanyRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.Exceptions.CompanyException;
import com.bezkoder.springjwt.security.Exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CategoriesRepos categoriesRepos;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository, CategoriesRepos categoriesRepos) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.categoriesRepos = categoriesRepos;
    }

    public List<CompanyResponseDto> getAll() {
        return companyRepository.findAll().stream()
                .map(Company::toResponseDto)
                .toList();
    }

    public List<UserCompanyResponseDto> getUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserCompanyResponseDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .companyId(user.getCompany() == null ? null : user.getCompany().getId())
                        .companyName(user.getCompany() == null ? null : user.getCompany().getName())
                        .build())
                .toList();
    }

    public Company create(CompanyCreateRequest request) {
        String name = request == null ? null : request.getName();
        if (name == null || name.isBlank()) {
            throw new CompanyException("Company name is required");
        }

        String normalizedName = name.trim();
        if (companyRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new CompanyException("Company already exists");
        }

        Company company = Company.builder()
                .name(normalizedName)
                .build();

        if (companyRepository.findAll().isEmpty()) {
            company.setDefaultForPublic(true);
        }

        return companyRepository.save(company);
    }

    public User linkUser(LinkUserCompanyRequest request) {
        if (request == null || request.getUserId() == null) {
            throw new UserNotFoundException("User not found");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Company company = request.getCompanyId() == null
                ? null
                : companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new CompanyException("Company not found"));

        user.setCompany(company);
        User savedUser = userRepository.save(user);
        if (company != null) {
            categoriesRepos.assignLegacyUserCategoriesToCompany(user.getId(), company.getId());
        }
        return savedUser;
    }

    public Company getDefaultForPublic() {
        return companyRepository.findFirstByDefaultForPublicTrue()
                .orElseThrow(() -> new CompanyException("Public company is not selected"));
    }

    public Company setDefaultForPublic(Long companyId) {
        Company selected = companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyException("Company not found"));

        List<Company> defaults = companyRepository.findAllByDefaultForPublicTrue();
        defaults.forEach(company -> {
            company.setDefaultForPublic(false);
            companyRepository.save(company);
        });

        selected.setDefaultForPublic(true);
        return companyRepository.save(selected);
    }

}
