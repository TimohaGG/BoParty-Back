package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Position.Category;
import com.bezkoder.springjwt.payload.response.Positions.CategoryResponseDto;
import com.bezkoder.springjwt.repository.CategoriesRepos;
import com.bezkoder.springjwt.security.Exceptions.NoContentException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {
    private final CategoriesRepos categoriesRepos ;
    public CategoriesService(CategoriesRepos igsService) {
        this.categoriesRepos = igsService;
    }

    public List<CategoryResponseDto> getAll(long userId){
        List<CategoryResponseDto> res = this.categoriesRepos.findAllByUserId(userId).stream().map(Category::toResponseDto).toList();
        if(res.isEmpty()){
            throw new NoContentException("There are no categories");
        }
        return res;
    }
}
