package com.example.pizza_backend.api.controller;

import com.example.pizza_backend.api.dto.request.product.ProductRequest;
import com.example.pizza_backend.api.dto.request.product.ProductSearchRequest;
import com.example.pizza_backend.api.dto.response.CategoryResponse;
import com.example.pizza_backend.api.dto.response.ProductResponse;
import com.example.pizza_backend.api.dto.response.PromotionProductResponse;
import com.example.pizza_backend.service.CategoryService;
import com.example.pizza_backend.service.ProductService;
import com.example.pizza_backend.service.PromotionProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final PromotionProductService promoService;
    private final CategoryService categoryService;
    @Autowired
    public ProductController(ProductService productService, PromotionProductService promoService, CategoryService categoryService) {
        this.productService = productService;
        this.promoService = promoService;
        this.categoryService = categoryService;
    }


    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllProducts(@ModelAttribute ProductSearchRequest productSearchReq) {
        List<ProductResponse> products = productService.getAllProducts(productSearchReq);
        List<PromotionProductResponse> rec = promoService.getAllRecommendedProducts();
        List<CategoryResponse> categories = categoryService.getAllCategories();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("products", products);
        response.put("recommendProductId", rec);
        response.put("categoriesDropdown", categories);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(
                                            HttpServletRequest request,
                                           @RequestPart("product") ProductRequest productRequest,
                                           @RequestPart("image") MultipartFile imageFile) throws IOException {
        String username = (String) request.getAttribute("username");
        String createLog = productService.createProduct(productRequest, imageFile, username);
        if (createLog == "success") {
            return  ResponseEntity.ok()
                    .body(Map.of("message", "create success"));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProduct(
            HttpServletRequest request,
            @RequestPart("product") ProductRequest productRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {
        String username = (String) request.getAttribute("username");
        String createLog="";
        if (imageFile != null && !imageFile.isEmpty()) {
            // ถ้ามีการส่งไฟล์มา, ให้ update ไฟล์ภาพ
            createLog = productService.updateProduct(productRequest, imageFile, username);
        } else {
            // ถ้าไม่มีไฟล์ภาพ, ให้ทำการ update โดยไม่มีการเปลี่ยนแปลงไฟล์
            createLog = productService.updateProduct(productRequest, null, username);
        }

        if (createLog == "success") {
            return  ResponseEntity.ok()
                    .body(Map.of("message", "update success"));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteProduct(
            @RequestBody ProductRequest productRequest) throws IOException {
        System.out.println(productRequest);
        String createLog= productService.deleteProduct(productRequest);
        if (createLog == "success") {
            return  ResponseEntity.ok()
                    .body(Map.of("message", "delete success"));
        }
        return ResponseEntity.badRequest().build();
    }

}
