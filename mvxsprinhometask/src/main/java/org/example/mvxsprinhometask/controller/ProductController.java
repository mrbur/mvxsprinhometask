package org.example.mvxsprinhometask.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mvxsprinhometask.entity.Product;
import org.example.mvxsprinhometask.servise.ProductService;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @JsonView(Views.Public.class)
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Page<Product> products = productService.getAllProducts(page, size, sortBy);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @JsonView(Views.Public.class)
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @JsonView(Views.Public.class)
    public ResponseEntity<Product> createProduct(@RequestBody @NonNull String productString) throws JsonProcessingException {
        Product createdProduct = productService.createProduct(objectMapper.readValue(productString, Product.class));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    @JsonView(Views.Public.class)
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id, 
            @RequestBody @NonNull String productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, objectMapper.readValue(productDetails, Product.class));
            return ResponseEntity.ok(updatedProduct);
        } catch (NoSuchElementException | JsonProcessingException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build(); // Статус 24
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}