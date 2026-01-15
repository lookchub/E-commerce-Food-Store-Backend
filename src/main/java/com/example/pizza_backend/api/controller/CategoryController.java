package com.example.pizza_backend.api.controller;

import com.example.pizza_backend.api.dto.request.category.CategoryRequest;
import com.example.pizza_backend.api.dto.request.category.CategorySearchRequest;
import com.example.pizza_backend.api.dto.request.product.ProductSearchRequest;
import com.example.pizza_backend.api.dto.response.CategoryResponse;
import com.example.pizza_backend.api.dto.response.ProductResponse;
import com.example.pizza_backend.service.CategoryService;
import com.example.pizza_backend.service.ProductService;
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
@RequestMapping("/category")
public class CategoryController {

    private ProductService productService;
    private CategoryService categoryService;

    @Autowired
    public void setProductService(ProductService productService,  CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllCategories(@ModelAttribute ProductSearchRequest req) {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        List<ProductResponse> products = productService.getAllProducts(req);


        Map<String, Object> response = new LinkedHashMap<>();
        response.put("categories", categories);
        response.put("products", products);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllCategoriesOnly(@ModelAttribute CategorySearchRequest req) {
        List<CategoryResponse> categories = categoryService.getAllCategories(req);


        Map<String, Object> response = new LinkedHashMap<>();
        response.put("categories", categories);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/create")
    public ResponseEntity<?> createProduct(
            HttpServletRequest request,
            @RequestPart("category") CategoryRequest categoryRequest,
            @RequestPart("image") MultipartFile imageFile) throws IOException {
        String username = (String) request.getAttribute("username");
        String createLog = categoryService.createCategory(categoryRequest, imageFile, username);
        if (createLog == "success") {
            return  ResponseEntity.ok()
                    .body(Map.of("message", "create success"));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProduct(
            HttpServletRequest request,
            @RequestPart(value = "category") CategoryRequest categoryRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {
        String username = (String) request.getAttribute("username");
        String createLog="";
        if (imageFile != null && !imageFile.isEmpty()) {
            // ถ้ามีการส่งไฟล์มา, ให้ update ไฟล์ภาพ
            createLog = categoryService.updateCategory(categoryRequest, imageFile, username);
        } else {
            // ถ้าไม่มีไฟล์ภาพ, ให้ทำการ update โดยไม่มีการเปลี่ยนแปลงไฟล์
            createLog = categoryService.updateCategory(categoryRequest, null, username);
        }

        if (createLog == "success") {
            return  ResponseEntity.ok()
                    .body(Map.of("message", "update success"));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteProduct(
            @RequestBody CategoryRequest categoryRequest) throws IOException {
        String createLog= categoryService.deleteCategory(categoryRequest);
        if (createLog == "success") {
            return  ResponseEntity.ok()
                    .body(Map.of("message", "delete success"));
        }
        return ResponseEntity.badRequest().build();
    }
}
