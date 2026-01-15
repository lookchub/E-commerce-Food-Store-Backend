package com.example.pizza_backend.api.dto.request.category;

import lombok.Data;

@Data
public class CategoryRequest {
    private Long categoryId;
    private String categoryName;
    private Integer categoryPriority;
    private String categoryProductPath;
}
