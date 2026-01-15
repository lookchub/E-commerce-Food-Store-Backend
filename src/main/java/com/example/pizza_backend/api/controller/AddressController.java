package com.example.pizza_backend.api.controller;

import com.example.pizza_backend.api.dto.request.AddressRequest;
import com.example.pizza_backend.api.dto.response.AddressResponse;
import com.example.pizza_backend.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/address")
public class AddressController {

    private AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAddress(HttpServletRequest request) {
        Long profileId = (Long) request.getAttribute("profile_id");
        AddressResponse address = addressService.getAddressByProfileId(profileId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("address", address);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateAddress(@RequestBody AddressRequest addressRequest) {
        addressService.updateAddress(addressRequest);
        return ResponseEntity.ok(Map.of("message", "Update address successfully"));
    }
}
