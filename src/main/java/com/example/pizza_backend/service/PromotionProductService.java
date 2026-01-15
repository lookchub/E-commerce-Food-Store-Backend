package com.example.pizza_backend.service;

import com.example.pizza_backend.api.dto.request.PromotionProductRequest;
import com.example.pizza_backend.api.dto.response.PromotionProductResponse;
import com.example.pizza_backend.exception.IdNotFoundException;
import com.example.pizza_backend.mapper.PromotionProductMapper;
import com.example.pizza_backend.persistence.entity.Product;
import com.example.pizza_backend.persistence.entity.PromotionProduct;
import com.example.pizza_backend.persistence.repository.ProductRepository;
import com.example.pizza_backend.persistence.repository.PromotionProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class PromotionProductService{

    private final PromotionProductRepository promoProductRepository;
    private final ProductRepository productRepository;
    private final PromotionProductMapper promoMapper;

    @Autowired
    public PromotionProductService(PromotionProductRepository promoProductRepository, ProductRepository productRepository, PromotionProductMapper promoMapper) {
        this.promoProductRepository = promoProductRepository;
        this.productRepository = productRepository;
        this.promoMapper = promoMapper;
    }

    public List<PromotionProductResponse> getAllRecommendedProducts() {
        List<PromotionProduct> recommendedProducts = promoProductRepository.findAll();
        return recommendedProducts.stream()
                .map(r -> promoMapper.toPromotionProductResponse(r))
                .toList();
    }


    @Transactional
    public String createRecommended(PromotionProductRequest promoRequest) throws IOException {
        if (promoRequest.getProductId() == null) {
            throw new IllegalArgumentException("The given product Id cannot be null");
        }
        Product product = productRepository.findById(promoRequest.getProductId())
                .orElseThrow(() -> new IdNotFoundException("Product Not found"));
        PromotionProduct promoProduct = promoMapper.toPromotionProduct(promoRequest);
        String fileName = product.getProductImg();

        Path sourcePath = Paths.get("Images/product-photos/" + fileName);
        Path targetPath = Paths.get("Images/recommended-photos/" + fileName);
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        promoProduct.setProduct(product);
        promoProduct.setPromotionImg(fileName);
        promoProductRepository.save(promoProduct);
        return "success";
    }


    @Transactional
    public String deleteRecommended(PromotionProductRequest promoRequest) throws IOException {
        if (promoRequest.getRecommendedId() == null) {
            throw new IllegalArgumentException("The given recommended Id cannot be null");
        }
        Long recommendId = promoRequest.getRecommendedId();
        PromotionProduct recommendedProduct = promoProductRepository.findById(recommendId)
                .orElseThrow(() -> new IdNotFoundException("Product Not found"));
        String filename = recommendedProduct.getPromotionImgPath();

        promoProductRepository.deleteById(recommendId);
        FileStorageService.deleteFile("Images/recommended-photos/",filename);
        return "success";
    }
}
