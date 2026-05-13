package org.example.mvxsprinhometask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mvxsprinhometask.entity.Customer;
import org.example.mvxsprinhometask.entity.Order;
import org.example.mvxsprinhometask.entity.Product;
import org.example.mvxsprinhometask.repository.CustomerRepository;
import org.example.mvxsprinhometask.repository.OrderRepository;
import org.example.mvxsprinhometask.repository.ProductRepository;
import org.example.mvxsprinhometask.servise.OrderService;
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
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(OrderService.class)
@AutoConfigureDataJpa
@ActiveProfiles("test")
@Transactional
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Customer savedCustomer;
    private Product savedProduct1;
    private Product savedProduct2;

    @BeforeEach
    void dbSetup() {
        Customer customer = new Customer();
        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        customer.setContactNumber("12345");
        savedCustomer = customerRepository.save(customer);

        Product product1 = new Product();
        product1.setName("Smartphone");
        product1.setCount(1L);
        product1.setPrice(new BigDecimal("800.00"));
        savedProduct1 = productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Case");
        product2.setCount(2L);
        product2.setPrice(new BigDecimal("50.00"));
        savedProduct2 = productRepository.save(product2);
    }

    @Test
    void testCreateOrder() throws Exception {
        Order inputOrder = new Order();
        inputOrder.setCustomer(savedCustomer);

        List products = new ArrayList();
        products.add(savedProduct1);
        inputOrder.setProducts(products);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputOrder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").isNumber())
                .andExpect(jsonPath("$.products", hasSize(1)))
                .andExpect(jsonPath("$.products[0].name").value("Smartphone"));
    }

    @Test
    void testCreateOrderErrorNotEnoughProduct() throws Exception {
        Order inputOrder = new Order();
        inputOrder.setCustomer(savedCustomer);

        List products = new ArrayList();
        products.add(savedProduct1);
        products.add(savedProduct1);
        products.add(savedProduct1);
        products.add(savedProduct2);
        inputOrder.setProducts(products);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputOrder)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetOrderById() throws Exception {
        Order order = new Order();
        order.setCustomer(savedCustomer);
        List products = new ArrayList();
        products.add(savedProduct1);
        order.setProducts(products);
        Order savedOrder = orderRepository.save(order);

        mockMvc.perform(get("/api/v1/orders/{id}", savedOrder.getOrderId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(savedOrder.getOrderId()));
    }

    @Test
    void testGetAllOrders() throws Exception {
        Order order = new Order();
        order.setCustomer(savedCustomer);
        List products = new ArrayList();
        products.add(savedProduct1);
        order.setProducts(products);

        orderRepository.saveAndFlush(order);
        mockMvc.perform(get("/api/v1/orders")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].orderId").isNumber())
                .andExpect(jsonPath("$.content[0].products[0].name").value("Smartphone"));
    }

    @Test
    void testUpdateOrder() throws Exception {
        Order order = new Order();
        order.setCustomer(savedCustomer);
        List products = new ArrayList();
        products.add(savedProduct2);
        order.setProducts(products);
        Order savedOrder = orderRepository.save(order);

        Order updateDetails = new Order();
        updateDetails.setProducts(products);

        mockMvc.perform(put("/api/v1/orders/{id}", savedOrder.getOrderId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(savedOrder.getOrderId()))
                .andExpect(jsonPath("$.products[0].name").value("Case"));
    }

    @Test
    void testDeleteOrder() throws Exception {
        Order order = new Order();
        order.setCustomer(savedCustomer);
        orderRepository.save(order);

        mockMvc.perform(delete("/api/v1/orders/{id}", order.getOrderId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}