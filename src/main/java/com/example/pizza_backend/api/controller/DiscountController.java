package com.example.pizza_backend.api.controller;

import com.example.pizza_backend.api.dto.request.discountCode.DiscountCodeSearchRequest;
import com.example.pizza_backend.api.dto.response.DiscountCodeResponse;
import com.example.pizza_backend.service.DiscountCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/code")
public class DiscountController {

    private DiscountCodeService discountCodeService;

    @Autowired
    public DiscountController(DiscountCodeService discountCodeService) {
        this.discountCodeService = discountCodeService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> getCode(DiscountCodeSearchRequest request) {

        DiscountCodeResponse code = discountCodeService.getDiscountCode(request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("results", code);

        return ResponseEntity.ok(response);
    }
}
