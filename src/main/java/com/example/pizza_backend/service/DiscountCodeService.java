package com.example.pizza_backend.service;


import com.example.pizza_backend.api.dto.request.discountCode.DiscountCodeSearchRequest;
import com.example.pizza_backend.api.dto.response.DiscountCodeResponse;
import com.example.pizza_backend.exception.IdNotFoundException;
import com.example.pizza_backend.mapper.DiscountCodeMapper;
import com.example.pizza_backend.persistence.entity.DiscountCode;
import com.example.pizza_backend.persistence.entity.Profile;
import com.example.pizza_backend.persistence.repository.DiscountCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountCodeService{

    private DiscountCodeRepository discountCodeRepository;
    private DiscountCodeMapper discountCodeMapper;

    @Autowired
    public DiscountCodeService(DiscountCodeRepository discountCodeRepository, DiscountCodeMapper discountCodeMapper) {
        this.discountCodeRepository = discountCodeRepository;
        this.discountCodeMapper = discountCodeMapper;
    }


    public DiscountCodeResponse getDiscountCode(DiscountCodeSearchRequest req) {
        if (req.getCode() == null){
            throw new IllegalArgumentException("The given code cannot be null");
        }
        DiscountCode code = discountCodeRepository.findByCode(req.getCode())
                .orElseThrow(() -> new IdNotFoundException("Code not found"));
        return discountCodeMapper.toDiscountCodeDto(code);
    }
}
