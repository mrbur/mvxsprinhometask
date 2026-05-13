package org.example.mvxsprinhometask.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mvxsprinhometask.entity.Order;
import org.example.mvxsprinhometask.servise.OrderService;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @JsonView(Views.Public.class)
    public ResponseEntity<Page<Order>> getAllOrders(
            @PageableDefault(page = 0, size = 10, sort = "orderId") Pageable pageable) {
        Page<Order> ordersPage = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(ordersPage);
    }

    @GetMapping("/{id}")
    @JsonView(Views.Public.class)
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    @JsonView(Views.Public.class)
    public ResponseEntity<Order> createOrder(@RequestBody @NonNull String order) throws JsonProcessingException {
        Order createdOrder = orderService.createOrder(objectMapper.readValue(order, Order.class));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PutMapping("/{id}")
    @JsonView(Views.Public.class)
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody @NonNull String orderDetails) throws JsonProcessingException {
        Order updatedOrder = orderService.updateOrder(id, objectMapper.readValue(orderDetails, Order.class));
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}