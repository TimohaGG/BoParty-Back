package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Menu.StaffCategory;
import com.bezkoder.springjwt.payload.request.Menus.StaffCategoryRequest;
import com.bezkoder.springjwt.payload.response.Menu.StaffCategoryResponse;
import com.bezkoder.springjwt.repository.StaffCategoryRepos;
import com.bezkoder.springjwt.security.Exceptions.OrderCreateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class StaffCategoryService {
    private static final List<StaffCategoryResponse> BUILT_IN_CATEGORIES = List.of(
            StaffCategoryResponse.builtIn("Офіціанти", "WAITER"),
            StaffCategoryResponse.builtIn("Кухарі", "COOK")
    );

    private final StaffCategoryRepos staffCategoryRepos;

    public StaffCategoryService(StaffCategoryRepos staffCategoryRepos) {
        this.staffCategoryRepos = staffCategoryRepos;
    }

    public List<StaffCategoryResponse> getAll() {
        List<StaffCategoryResponse> customCategories = this.staffCategoryRepos.findAll()
                .stream()
                .map(StaffCategoryResponse::from)
                .toList();

        return java.util.stream.Stream.concat(BUILT_IN_CATEGORIES.stream(), customCategories.stream()).toList();
    }

    @Transactional
    public StaffCategoryResponse create(StaffCategoryRequest req) {
        String name = req.getName() == null ? "" : req.getName().trim();
        if (name.isBlank()) {
            throw new OrderCreateException("Category name is required");
        }

        String code = buildCode(name);
        if (this.staffCategoryRepos.existsByCodeIgnoreCase(code)) {
            throw new OrderCreateException("Staff category already exists");
        }

        StaffCategory category = new StaffCategory();
        category.setName(name);
        category.setCode(code);

        return StaffCategoryResponse.from(this.staffCategoryRepos.save(category));
    }

    private String buildCode(String name) {
        return "STAFF_" + name.trim()
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}]+", "_")
                .replaceAll("^_+|_+$", "");
    }
}
