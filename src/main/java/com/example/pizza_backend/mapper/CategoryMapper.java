package com.example.pizza_backend.mapper;


import com.example.pizza_backend.api.dto.request.category.CategoryRequest;
import com.example.pizza_backend.api.dto.response.CategoryResponse;
import com.example.pizza_backend.persistence.entity.Category;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    //CREATE
    @Mapping(target = "categoryImg", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    Category toCategory(CategoryRequest categoryInput, @Context String name);

    //UPDATE
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "categoryImg", ignore = true)
    void updateCategoryFromInput(CategoryRequest categoryInput, @MappingTarget Category category, @Context String name);

    //READ
    @Mapping(source = "categoryImgPath", target = "categoryImgPath")
    CategoryResponse toCategoryResponse(Category category);
}
