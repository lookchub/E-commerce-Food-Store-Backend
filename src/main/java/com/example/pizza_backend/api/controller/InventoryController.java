package com.example.pizza_backend.api.controller;

import com.example.pizza_backend.api.dto.request.InventoryRequest;
import com.example.pizza_backend.api.dto.request.product.ProductSearchRequest;
import com.example.pizza_backend.api.dto.response.InventoryResponse;
import com.example.pizza_backend.api.dto.response.ProductResponse;
import com.example.pizza_backend.service.InventoryTransactionService;
import com.example.pizza_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stock")
public class InventoryController {
    private final InventoryTransactionService inventoryService;
    private final ProductService productService;

    @Autowired
    public InventoryController(InventoryTransactionService inventoryService, ProductService productService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getStockTransaction(@ModelAttribute InventoryRequest req) {
        ProductSearchRequest productSearchReq = new ProductSearchRequest();
        productSearchReq.setProductName(req.getProductName());
        List<ProductResponse> products = productService.getAllProducts(productSearchReq);
        if (products.isEmpty()) {
            Map<String, Object> total = new LinkedHashMap<>();
            total.put("remaining", 0);
            total.put("stockOut", 0);

            List<Object> emptyStockTable = new ArrayList<>();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("TotalInventory", total);
            response.put("StockTable", emptyStockTable);  // ส่ง List ว่าง

            return ResponseEntity.ok(response);
        }

        Integer totalRemaining = inventoryService.getTotalInventory(products, 1, req); //เติม-ออก
        Integer totalOut = inventoryService.getTotalInventory(products, 2, req); //ขาย

        List<InventoryResponse> inventory = inventoryService.getInventory(products, req);

        Map<String, Object> total = new LinkedHashMap<>();
        total.put("remaining", totalRemaining);
        total.put("stockOut", totalOut);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("TotalInventory", total);
        response.put("StockTable", inventory);
        return ResponseEntity.ok(response);
    }
}
