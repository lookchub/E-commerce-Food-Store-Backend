package com.example.pizza_backend.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @OneToOne(mappedBy = "address")
    private Profile profile;

    private String phone;
    private String province;
    private String amphor;
    private String district;
    private String zipCode;
    private String addrNum;
    private String detail;
    private String receivedName;
}
