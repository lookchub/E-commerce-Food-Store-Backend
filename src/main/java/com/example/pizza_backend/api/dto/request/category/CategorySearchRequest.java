package com.example.pizza_backend.api.dto.request.category;

import lombok.Data;

@Data
public class CategorySearchRequest {
    private String categoryName;
    private Long categoryId;
}
