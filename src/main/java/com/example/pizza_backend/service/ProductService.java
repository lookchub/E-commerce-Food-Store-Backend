package com.example.pizza_backend.service;


import com.example.pizza_backend.api.dto.request.product.ProductRequest;
import com.example.pizza_backend.api.dto.request.product.ProductSearchRequest;
import com.example.pizza_backend.api.dto.response.ProductResponse;
import com.example.pizza_backend.exception.IdNotFoundException;
import com.example.pizza_backend.mapper.ProductMapper;
import com.example.pizza_backend.persistence.entity.Category;
import com.example.pizza_backend.persistence.entity.InventoryTransaction;
import com.example.pizza_backend.persistence.entity.Product;
import com.example.pizza_backend.persistence.repository.CategoryRepository;
import com.example.pizza_backend.persistence.repository.InventoryTransactionRepository;
import com.example.pizza_backend.persistence.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final InventoryTransactionRepository invtRepository;

    @Autowired
    public ProductService(ProductRepository productRepository,
                          ProductMapper productMapper,
                          CategoryRepository categoryRepository,
                          InventoryTransactionRepository invtRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.invtRepository = invtRepository;
    }


    public List<ProductResponse> getAllProducts(ProductSearchRequest req){
        List<Product> products = productRepository.searchProducts(
                req.getProductId(),
                req.getProductName(),
                req.getProductStock(),
                req.getProductPrice(),
                req.getCategoryId(),
                req.getIsActive()
        );
        
        return products.stream()
                .map(p -> productMapper.toProductResponse(p))
                .toList();
    }


    @Transactional
    public String createProduct(ProductRequest productRequest, MultipartFile imageFile, String username) throws IOException {
        if (productRequest.getCategoryId() == null) {
            throw new IllegalArgumentException("The given category Id cannot be null");
        }
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new IdNotFoundException("Category Not found"));

        Product product = productMapper.toProduct(productRequest, username);
        String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
        product.setProductImg(fileName);
        product.setCategory(category);
        productRepository.save(product);

        FileStorageService.saveFile("Images/product-photos/",imageFile,fileName);

        return "success";
    }


    @Transactional
    public String updateProduct(ProductRequest productRequest, MultipartFile imageFile, String username) throws IOException {
        if (productRequest.getProductId() == null) {
            throw new IllegalArgumentException("The given product Id cannot be null");
        }
        Long productId = productRequest.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IdNotFoundException("Product Not found"));

        if (productRequest.getProductStock() != null) {
            InventoryTransaction ivt = new InventoryTransaction();
            ivt.setProduct(product);
            ivt.setCreatedAt(LocalDateTime.now());
            Integer qtyChange = 0;
            if (productRequest.getStockType() == 1){
                qtyChange = productRequest.getProductStock() - product.getProductStock();
            }
            if (productRequest.getStockType() == 3){
                qtyChange = product.getProductStock() - productRequest.getProductStock();
            }
            ivt.setQtyChange(qtyChange);
            ivt.setTransactionType(productRequest.getStockType());
            invtRepository.save(ivt);
        }

        productMapper.updateProductFromInput(productRequest, product, username);
        if (productRequest.getCategoryId() != null){
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new IdNotFoundException("Category Not found"));
            product.setCategory(category);
        }

        if (imageFile != null && !imageFile.isEmpty()){
            String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            FileStorageService.deleteFile("Images/product-photos/",product.getProductImg());
            product.setProductImg(fileName);
            FileStorageService.saveFile("Images/product-photos/",imageFile,fileName);
        }


        productRepository.save(product);


        return "success";
    }


    @Transactional
    public String deleteProduct(ProductRequest productRequest) throws IOException {
        if (productRequest.getProductId() == null) {
            throw new IllegalArgumentException("The given product Id cannot be null");
        }
        Long productId = productRequest.getProductId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IdNotFoundException("Product Not found"));
        String filename = product.getProductImg();

        productRepository.deleteById(productId);
        FileStorageService.deleteFile("Images/product-photos/",filename);
        return "success";
    }


    public Product getProductById(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new IdNotFoundException("Product Not found"));
    }


}
