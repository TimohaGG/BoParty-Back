package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Position.Category;
import com.bezkoder.springjwt.models.User.User;
import com.bezkoder.springjwt.payload.request.Position.PositionCategoryCreateReq;
import com.bezkoder.springjwt.payload.response.Positions.CategoryResponseDto;
import com.bezkoder.springjwt.repository.CategoriesRepos;
import com.bezkoder.springjwt.security.Exceptions.CategoryCreateException;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {
    private final CategoriesRepos categoriesRepos ;
    private final UserDetailsServiceImpl userDetailsService;
    public CategoriesService(CategoriesRepos igsService, UserDetailsServiceImpl userDetailsService) {
        this.categoriesRepos = igsService;
        this.userDetailsService = userDetailsService;
    }

    public List<CategoryResponseDto> getAll(long userId){
        List<CategoryResponseDto> res = this.categoriesRepos.findAllByUserId(userId).stream().map(Category::toResponseDto).toList();
        if(res.isEmpty()){
            throw new NoContentException("There are no categories");
        }
        return res;
    }

    public Category addCategory(PositionCategoryCreateReq req) {
        User user = this.userDetailsService.GetUserById(req.userId);
        Category category = Category.builder()
                .name(req.name)
                .user(user)
                .sortingOrder(categoriesRepos.findAllByUserId(req.userId).size()+1)
                .build();
        try{
            return this.categoriesRepos.save(category);
        }catch(Exception e){
            throw new CategoryCreateException("Can't create category");
        }

    }
}
