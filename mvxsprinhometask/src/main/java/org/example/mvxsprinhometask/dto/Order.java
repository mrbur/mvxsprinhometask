package org.example.mvxsprinhometask.dto;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import org.example.mvxsprinhometask.controller.Views;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;

    @JsonView(Views.Public.class)
    private Long userId;

    @JsonView(Views.Public.class)
    private Long price;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}