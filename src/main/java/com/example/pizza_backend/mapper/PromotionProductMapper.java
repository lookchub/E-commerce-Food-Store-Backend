package com.example.pizza_backend.mapper;

import com.example.pizza_backend.api.dto.request.PromotionProductRequest;
import com.example.pizza_backend.api.dto.response.PromotionProductResponse;
import com.example.pizza_backend.persistence.entity.PromotionProduct;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface PromotionProductMapper {
    PromotionProductMapper INSTANCE = Mappers.getMapper(PromotionProductMapper.class);

    //CREATE
    @Mapping(target = "promotionId", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "promotionImg", ignore = true)
    PromotionProduct toPromotionProduct(PromotionProductRequest recommendedInput);

    //READ
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "promotionImgPath", target = "promotionImgPath")
    PromotionProductResponse toPromotionProductResponse(PromotionProduct recommendedProduct);
}
