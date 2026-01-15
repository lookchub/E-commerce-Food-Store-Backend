package com.example.pizza_backend.api.dto.request;

import lombok.Data;

@Data
public class AddressRequest {
    private Long addressId;
    private String phone;
    private String province;
    private String amphor;
    private String district;
    private String zipCode;
    private String addrNum;
    private String detail;
    private String receivedName;
}
