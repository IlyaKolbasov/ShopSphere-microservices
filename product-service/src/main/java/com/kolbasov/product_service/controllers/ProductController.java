package com.kolbasov.product_service.controllers;

import com.kolbasov.product_service.dto.ProductRequest;
import com.kolbasov.product_service.dto.ProductResponse;
import com.kolbasov.product_service.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/product")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest){
        productService.createProduct(productRequest);

    }


    @GetMapping("/get")
    public ResponseEntity<List<ProductResponse>>getProduct(){
      List<ProductResponse> productResponses= productService.getAllProduct();
      return ResponseEntity.ok(productResponses);
    }
}
