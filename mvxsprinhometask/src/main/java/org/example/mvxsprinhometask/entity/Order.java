package org.example.mvxsprinhometask.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import org.example.mvxsprinhometask.controller.Views;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "orderId"
)
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long orderId;

    @JsonView(Views.Public.class)
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    @JsonView(Views.Public.class)
    private List<Product> products = new ArrayList<>();

    @JsonIgnore
    @JsonView(Views.Public.class)
    public BigDecimal getTotalPrice() {
        if (this.products == null || this.products.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return this.products.stream()
                .map(Product::getPrice)
                .filter(price -> price != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}