package com.example.pizza_backend.api.controller;


import com.example.pizza_backend.api.dto.request.product.ProductSearchRequest;
import com.example.pizza_backend.api.dto.response.CategoryResponse;
import com.example.pizza_backend.api.dto.response.ProductResponse;
import com.example.pizza_backend.api.dto.response.PromotionProductResponse;
import com.example.pizza_backend.service.CategoryService;
import com.example.pizza_backend.service.ProductService;
import com.example.pizza_backend.service.PromotionProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/home")
public class HomeController {

    private ProductService productService;
    private CategoryService categoryService;
    private PromotionProductService promoService;

    @Autowired
    public void setProductService(ProductService productService,  CategoryService categoryService, PromotionProductService promoService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.promoService = promoService;
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getHomeInfo(@ModelAttribute ProductSearchRequest req) {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        List<ProductResponse> products = productService.getAllProducts(req);
        List<PromotionProductResponse> recommends = promoService.getAllRecommendedProducts();


        //จัดกลุ่ม product ตาม categoryId
        Map<Long, List<ProductResponse>> productsByCategory = products.stream()
                .collect(Collectors.groupingBy(
                p -> p.getCategoryId()
                ));
        //เอา product ที่จัดกลุ่มไว้ ยัดใส่แต่ละ category
        List<Map<String, Object>> results = categories.stream()
                .map(c -> Map.of(
                        "category", c,
                        "products", productsByCategory.getOrDefault(c.getCategoryId(), List.of())
                ))
                .toList();


        Map<String, Object> response = new LinkedHashMap<>();
        response.put("results", results);
        response.put("recommendedProducts", recommends);

        return ResponseEntity.ok(response);
    }

}
