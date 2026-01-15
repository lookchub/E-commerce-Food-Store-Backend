package com.example.pizza_backend.service;


import com.example.pizza_backend.api.dto.request.category.CategoryRequest;
import com.example.pizza_backend.api.dto.request.category.CategorySearchRequest;
import com.example.pizza_backend.api.dto.response.CategoryResponse;
import com.example.pizza_backend.exception.IdNotFoundException;
import com.example.pizza_backend.mapper.CategoryMapper;
import com.example.pizza_backend.persistence.entity.Category;
import com.example.pizza_backend.persistence.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService{

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }


    public List<CategoryResponse> getAllCategories(CategorySearchRequest req) {
        List<Category> categories = new ArrayList<>();
        if (req != null) {
            categories = categoryRepository.searchCategory(
                    req.getCategoryId(),
                    req.getCategoryName()
            );
        } else {
            categories = categoryRepository.findAll();
        }
        return categories.stream()
                .map(c-> categoryMapper.toCategoryResponse(c))
                .toList();
    }

    public List<CategoryResponse> getAllCategories() {
        return getAllCategories(null);  // เหมือนเดิมเลย
    }
    
    @Transactional
    public String createCategory(CategoryRequest categoryRequest, MultipartFile imageFile, String username) throws IOException {
        Category category = categoryMapper.toCategory(categoryRequest, username);
        String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
        category.setCategoryImg(fileName);
        categoryRepository.save(category);

        FileStorageService.saveFile("Images/category-photos/",imageFile,fileName);

        return "success";
    }


    @Transactional
    public String updateCategory(CategoryRequest CategoryRequest, MultipartFile imageFile, String username) throws IOException {
        if (CategoryRequest.getCategoryId() == null) {
            throw new IllegalArgumentException("The given category Id cannot be null");
        }
        Long categoryId = CategoryRequest.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IdNotFoundException("Category Not found"));
        categoryMapper.updateCategoryFromInput(CategoryRequest, category, username);

        if (imageFile != null && !imageFile.isEmpty()){
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            FileStorageService.deleteFile("Images/category-photos/",category.getCategoryImg());
            category.setCategoryImg(fileName);
            FileStorageService.saveFile("Images/category-photos/",imageFile,fileName);
        }

        categoryRepository.save(category);
        return "success";
    }

    @Transactional
    public String deleteCategory(CategoryRequest CategoryRequest) throws IOException {
        if (CategoryRequest.getCategoryId() == null) {
            throw new IllegalArgumentException("The given category Id cannot be null");
        }
        Long categoryId = CategoryRequest.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IdNotFoundException("Category Not found"));
        String filename = category.getCategoryImg();

        categoryRepository.deleteById(categoryId);
        FileStorageService.deleteFile("Images/category-photos/",filename);
        return "success";
    }
}
