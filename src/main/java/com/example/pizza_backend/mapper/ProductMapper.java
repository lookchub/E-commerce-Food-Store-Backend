package com.example.pizza_backend.mapper;

import com.example.pizza_backend.api.dto.request.product.ProductRequest;
import com.example.pizza_backend.api.dto.response.ProductResponse;
import com.example.pizza_backend.persistence.entity.Product;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    //CREATE
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "productImg", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdBy", expression = "java(name)")
    @Mapping(target = "productId", ignore = true)
    Product toProduct(ProductRequest productInput, @Context String name);

    //UPDATE
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)     // handle manually
    @Mapping(target = "productImg", ignore = true)   // handle manually
    @Mapping(target = "productId", ignore = true)   // for update
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedBy", expression = "java(name)")
    void updateProductFromInput(ProductRequest productInput, @MappingTarget Product product, @Context String name);

    //READ
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "category.categoryName", target = "categoryName")
    @Mapping(source = "productImgPath", target = "productImgPath")
    ProductResponse toProductResponse(Product product);
}
