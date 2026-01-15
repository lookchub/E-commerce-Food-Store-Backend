package com.example.pizza_backend.api.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InventoryRequest {
    private LocalDate startTime;
    private LocalDate endTime;
    private String productName;
}
