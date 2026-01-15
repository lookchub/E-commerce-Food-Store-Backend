package com.example.pizza_backend.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String categoryName;
    private String categoryImg;
    private String categoryProductPath;
    private Long categoryPriority;

    @Transient
    public String getCategoryImgPath(){
        if (categoryImg == null) return null;

        return "/Images/category-photos/"+ categoryImg;
    }
}
