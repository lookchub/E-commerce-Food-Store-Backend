package com.example.pizza_backend.service;


import com.example.pizza_backend.api.dto.request.InventoryRequest;
import com.example.pizza_backend.api.dto.response.InventoryResponse;
import com.example.pizza_backend.api.dto.response.ProductResponse;
import com.example.pizza_backend.persistence.repository.InventoryTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class InventoryTransactionService{
    private final InventoryTransactionRepository invtRepository;

    @Autowired
    public InventoryTransactionService(InventoryTransactionRepository invtRepository) {
        this.invtRepository = invtRepository;
    }

    private LocalDateTime resolveStartTime(LocalDate startTime) {
        if (startTime == null) {
            return LocalDateTime.of(2024, 1, 1, 0, 0);
        }
        return startTime.atStartOfDay();
    }

    private LocalDateTime resolveEndTime(LocalDate endTime) {
        if (endTime == null) {
            return LocalDateTime.now();
        }
        return endTime.atTime(LocalTime.MAX);
    }


    public Integer getTotalInventory(List<ProductResponse> products, Integer type, InventoryRequest req) {
        Integer totalInventory = 0;

        if (products.isEmpty()) {
            return 0;
        }

        LocalDateTime start = resolveStartTime(req.getStartTime());
        LocalDateTime end = resolveEndTime(req.getEndTime());

        if (type == 1) {
            for (ProductResponse product : products) {
                Integer stock = invtRepository.sumQtyChangeByProductIdAndType(product.getProductId(), 1, start, end);
                Integer out = invtRepository.sumQtyChangeByProductIdAndType(product.getProductId(), 3, start, end);

                stock = (stock != null) ? stock : 0;
                out = (out != null) ? out : 0;
                Integer temp = stock - out;
                totalInventory += (temp != null ? temp : 0);
            }
        }

        if (type == 2) {
            for (ProductResponse product : products) {
                Integer sold = invtRepository.sumQtyChangeByProductIdAndType(product.getProductId(), 2, start, end);

                sold = (sold != null) ? sold : 0;
                totalInventory += (sold != null ? sold : 0);
            }
        }
        return totalInventory;
    }


    public List<InventoryResponse> getInventory(List<ProductResponse> products, InventoryRequest req) {

        if (products.isEmpty()) {
            return Collections.emptyList();
        }

        LocalDateTime start = resolveStartTime(req.getStartTime());
        LocalDateTime end = resolveEndTime(req.getEndTime());

        List<InventoryResponse> InventoryResponseList = new ArrayList<>();

        // วนลูปผ่านแต่ละ product ที่เสิร์ชเจอ
        for (ProductResponse product : products) {
            Integer stock = invtRepository.sumQtyChangeByProductIdAndType(product.getProductId(), 1, start, end);
            Integer sold = invtRepository.sumQtyChangeByProductIdAndType(product.getProductId(), 2, start, end);
            Integer out = invtRepository.sumQtyChangeByProductIdAndType(product.getProductId(), 3, start, end);

            stock = (stock != null) ? stock : 0;
            sold = (sold != null) ? sold : 0;
            out = (out != null) ? out : 0;
            Integer temp = stock - sold - out;

            InventoryResponse inventory = InventoryResponse.builder()
                    .productName(product.getProductName())
                    .productId(product.getProductId())
                    .stock(temp)
                    .sold(sold)
                    .categoryName(product.getCategoryName())
                    .price(product.getProductPrice())
                    .build();
            InventoryResponseList.add(inventory);
        }

        return InventoryResponseList;
    }
}
