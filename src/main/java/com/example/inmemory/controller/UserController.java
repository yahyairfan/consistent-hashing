package com.example.inmemory.controller;

import com.example.inmemory.dao.entity.User;
import com.example.inmemory.service.dto.UserDTO;
import com.example.inmemory.service.UserService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<Void> createUser(@RequestBody User user) {
        userService.saveUser(user);
        return ResponseEntity.created(URI.create("/user")).build();
    }

    @GetMapping("/user")
    public ResponseEntity<UserDTO> createUser(@NotNull @RequestParam("username") String userName) {
        return ResponseEntity.ok(userService.getUser(userName));
    }
}
