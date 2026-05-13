package org.example.mvxsprinhometask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mvxsprinhometask.entity.Product;
import org.example.mvxsprinhometask.repository.ProductRepository;
import org.example.mvxsprinhometask.servise.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.AutoConfigureDataJpa;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(ProductService.class)
@AutoConfigureDataJpa
@ActiveProfiles("test")
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        sampleProduct = new Product();
        sampleProduct.setName("Клавиатура");
        sampleProduct.setDescription("Механическая RGB клавиатура");
        sampleProduct.setCount(15L);
        sampleProduct.setPrice(new BigDecimal("4500.00"));
    }

    @Test
    void createProduct() throws Exception {
        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Клавиатура"))
                .andExpect(jsonPath("$.price").value(4500.00));
    }

    @Test
    void getProductById() throws Exception {
        Product savedProduct = productRepository.save(sampleProduct);

        mockMvc.perform(get("/api/v1/products/{id}", savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedProduct.getId()))
                .andExpect(jsonPath("$.name").value("Клавиатура"));
    }

    @Test
    void getProductById_error() throws Exception {
        mockMvc.perform(get("/api/v1/products/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct() throws Exception {
        Product savedProduct = productRepository.save(sampleProduct);

        mockMvc.perform(delete("/api/v1/products/{id}", savedProduct.getId()))
                .andExpect(status().isNoContent());

        assert(!productRepository.existsById(savedProduct.getId()));
    }
}