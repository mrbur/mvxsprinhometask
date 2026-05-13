package org.example.mvxsprinhometask.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonView;
import org.example.mvxsprinhometask.controller.Views;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;

    @Column(nullable = false, unique = true)
    @JsonView(Views.Public.class)
    private String username;

    @Column(nullable = false)
    @JsonView(Views.Private.class)
    private String address;

    @Column(nullable = true, unique = true)
    @JsonView(Views.Private.class)
    private String phone;

    @Column(nullable = false, unique = true)
    @JsonView(Views.Private.class)
    private String email;

    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}