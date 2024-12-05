package com.kolbasov.product_service.service;

import com.kolbasov.product_service.dto.ProductRequest;
import com.kolbasov.product_service.dto.ProductResponse;
import com.kolbasov.product_service.entity.Product;
import com.kolbasov.product_service.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void createProduct (ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
    }

    public List<ProductResponse> getAllProduct() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::productToProductResponse).collect(Collectors.toList());
    }

    private ProductResponse productToProductResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }


}
