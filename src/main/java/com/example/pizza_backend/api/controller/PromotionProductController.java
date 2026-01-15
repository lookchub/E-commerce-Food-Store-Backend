package com.example.pizza_backend.api.controller;

import com.example.pizza_backend.api.dto.request.PromotionProductRequest;
import com.example.pizza_backend.service.PromotionProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/recommend")
public class PromotionProductController {

    private final PromotionProductService promoService;

    @Autowired
    public PromotionProductController(PromotionProductService promoService) {
        this.promoService = promoService;
    }

//    @GetMapping("/list")
//    public ResponseEntity<Map<String, Object>> getAllRecommended() {
//        List<RecommendedProductDto> rec = recommendedService.getAllRecommendedProducts();
//        Map<String, Object> response = new LinkedHashMap<>();
//        response.put("recommended", rec);
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/create")
    public ResponseEntity<?> createRecommended(
            @RequestBody PromotionProductRequest promoReq) throws IOException {
        String createLog = promoService.createRecommended(promoReq);
        if (createLog == "success") {
            return  ResponseEntity.ok()
                    .body(Map.of("message", "create success"));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteRecommended(
            @RequestBody PromotionProductRequest promoReq) throws IOException {
        String createLog= promoService.deleteRecommended(promoReq);
        if (createLog == "success") {
            return  ResponseEntity.ok()
                    .body(Map.of("message", "delete success"));
        }
        return ResponseEntity.badRequest().build();
    }
}
