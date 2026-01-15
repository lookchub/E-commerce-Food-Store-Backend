package com.example.pizza_backend.mapper;

import com.example.pizza_backend.api.dto.response.DiscountCodeResponse;
import com.example.pizza_backend.persistence.entity.DiscountCode;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface DiscountCodeMapper {
    DiscountCodeMapper INSTANCE = Mappers.getMapper(DiscountCodeMapper.class);

    //READ
    @Mapping(source = "promotionProduct.product.productId", target = "productId")
    DiscountCodeResponse toDiscountCodeDto(DiscountCode discountCode);
}
