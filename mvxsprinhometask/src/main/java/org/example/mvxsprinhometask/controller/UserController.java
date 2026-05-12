package org.example.mvxsprinhometask.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mvxsprinhometask.entity.User;
import org.example.mvxsprinhometask.servise.UserService;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/public")
    @JsonView(Views.Public.class)
    public ResponseEntity<User> getPublicProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/private")
    @JsonView(Views.Private.class)
    public ResponseEntity<User> getFullProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<Page<User>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @PostMapping
    @JsonView(Views.Private.class)
    public ResponseEntity<User> createUser(@RequestBody @NonNull String user) throws JsonProcessingException {
        User createdUser = userService.create(objectMapper.readValue(user, User.class));
        if(createdUser != null) {
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        }
        throw new NullPointerException("Cant save user");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.remove(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @JsonView(Views.Private.class)
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody @NonNull String userDetails) throws JsonProcessingException {
        User updatedUser = userService.update(id, objectMapper.readValue(userDetails, User.class));
        return ResponseEntity.ok(updatedUser);
    }
}