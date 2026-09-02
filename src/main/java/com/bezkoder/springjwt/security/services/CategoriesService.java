package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Position.Category;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.request.Position.PositionCategoryCreateReq;
import com.bezkoder.springjwt.payload.response.Positions.CategoryResponseDto;
import com.bezkoder.springjwt.repository.CompanyRepository;
import com.bezkoder.springjwt.repository.CategoriesRepos;
import com.bezkoder.springjwt.security.Exceptions.CategoryCreateException;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {
    private final CategoriesRepos categoriesRepos ;
    private final UserDetailsServiceImpl userDetailsService;
    private final CompanyRepository companyRepository;
    private final CompanyService companyService;

    public CategoriesService(CategoriesRepos igsService, UserDetailsServiceImpl userDetailsService, CompanyRepository companyRepository, CompanyService companyService) {
        this.categoriesRepos = igsService;
        this.userDetailsService = userDetailsService;
        this.companyRepository = companyRepository;
        this.companyService = companyService;
    }

    public List<CategoryResponseDto> getAllForCurrentCompany() {
        User user = this.userDetailsService.getCurrentUser();
        if (user == null || user.getCompany() == null) {
            throw new NoContentException("User is not linked to a company");
        }

        return getAllForCompany(user.getCompany().getId());
    }

    public List<CategoryResponseDto> getAllForPublicCompany() {
        return getAllForCompany(this.companyService.getDefaultForPublic().getId());
    }

    private List<CategoryResponseDto> getAllForCompany(Long companyId) {
        List<CategoryResponseDto> res = this.categoriesRepos.findAllByCompanyIdOrdered(companyId)
                .stream()
                .map(Category::toResponseDto)
                .toList();
        if (res.isEmpty()) {
            throw new NoContentException("There are no categories");
        }
        return res;
    }

    public Category addCategory(PositionCategoryCreateReq req) {
        User user = this.userDetailsService.getCurrentUser();
        if (user == null) {
            throw new NoContentException("User not found");
        }

        var company = req.companyId == null
                ? user.getCompany()
                : this.companyRepository.findById(req.companyId).orElseThrow(() -> new NoContentException("Company not found"));
        if (company == null) {
            throw new NoContentException("User is not linked to a company");
        }

        Category category = Category.builder()
                .name(req.name)
                .company(company)
                .sortingOrder(categoriesRepos.findAllByCompanyId(company.getId()).size()+1)
                .build();
        try{
            return this.categoriesRepos.save(category);
        }catch(Exception e){
            throw new CategoryCreateException("Can't create category");
        }

    }
}
