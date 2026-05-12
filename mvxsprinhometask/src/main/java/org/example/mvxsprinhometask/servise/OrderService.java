package org.example.mvxsprinhometask.servise;

import jakarta.persistence.EntityNotFoundException;
import org.example.mvxsprinhometask.entity.Customer;
import org.example.mvxsprinhometask.entity.Order;
import org.example.mvxsprinhometask.entity.Product;
import org.example.mvxsprinhometask.repository.CustomerRepository;
import org.example.mvxsprinhometask.repository.OrderRepository;
import org.example.mvxsprinhometask.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(Order order) {
        if (order.getCustomer() == null || order.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Order must have a customer with a valid ID");
        }
        Customer customer = customerRepository.findById(order.getCustomer().getId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + order.getCustomer().getId()));
        order.setCustomer(customer);

        if (order.getProducts() != null && !order.getProducts().isEmpty()) {
            List<Product> persistentProducts = new ArrayList<>();
            Map<Long, Long> productCountList = order.getProducts().stream().collect(Collectors.groupingBy(Product::getId, Collectors.counting()));
            productCountList.forEach((id, count) -> {if(!productRepository.isProductAvailable(id, count))throw new IllegalArgumentException();});
            for (Product product : order.getProducts()) {
                Product existingProduct = productRepository.findByIdForUpdate(product.getId())
                        .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + product.getId()));
                persistentProducts.add(existingProduct);
            }
            order.setProducts(persistentProducts);
        } else {
            order.setProducts(new ArrayList<>());
        }
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        if(orderId == null || orderId < 0) {
            throw new IllegalArgumentException("bad product id");
        }
        return orderRepository.findByIdWithProducts(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
    }

    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable parameter cannot be null");
        }
        return orderRepository.findAllOrdersPaged(pageable);
    }

    @Transactional
    public Order updateOrder(Long orderId, Order orderDetails) {
        if(orderId == null || orderDetails == null || orderDetails.getTotalPrice() == null
            || orderDetails.getProducts() == null || orderDetails.getProducts().isEmpty()) {
            throw new IllegalArgumentException("bad product data");
        }
        Order existingOrder = orderRepository.findByIdWithProducts(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        if (orderDetails.getCustomer() != null && orderDetails.getCustomer().getId() != null) {
            Customer customer = customerRepository.findById(orderDetails.getCustomer().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + orderDetails.getCustomer().getId()));
            existingOrder.setCustomer(customer);
        }

        if (orderDetails.getProducts() != null) {
            Set<Product> updatedProducts = new HashSet<>();
            for (Product product : orderDetails.getProducts()) {
                if (product.getId() != null) {
                    Product existingProduct = productRepository.findById(product.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + product.getId()));
                    updatedProducts.add(existingProduct);
                }
            }
            existingOrder.getProducts().clear();
            existingOrder.getProducts().addAll(updatedProducts);
        }

        return orderRepository.save(existingOrder);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        if(orderId == null || orderId < 0) {
            throw new IllegalArgumentException("bad product id");
        }
        if (!orderRepository.existsById(orderId)) {
            throw new EntityNotFoundException("Order not found with id: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }
}