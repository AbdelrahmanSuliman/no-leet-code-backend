package com.example.noleetcode.controllers;

import com.example.noleetcode.dto.RegisterUserDto;
import com.example.noleetcode.models.User;
import com.example.noleetcode.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody RegisterUserDto registerUserDto) {
        String token = String.valueOf(authService.register(registerUserDto));
        return ResponseEntity.ok(token);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        // Extract the username from the Authentication object
        String username = authentication.getName();

        // Find the user by the username
        User user = authService.getCurrentUser(username);

        // Return the user details
        return ResponseEntity.ok(user);
    }

}
