package com.example.pizza_backend.api.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionProductResponse {
    private Long promotionId;
    private Long productId;
    private String promotionImgPath;
}
