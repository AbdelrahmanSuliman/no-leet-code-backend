package com.example.noleetcode.controllers;

import com.example.noleetcode.dto.LoginUserDto;
import com.example.noleetcode.dto.RegisterUserDto;
import com.example.noleetcode.models.User;
import com.example.noleetcode.services.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDto registerUserDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }
        String token = String.valueOf(authService.register(registerUserDto));
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginUserDto loginUserDto) {
        String token = authService.login(loginUserDto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        // Log the authentication object for debugging
        logger.debug("Authentication: {}", authentication);

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Unauthorized if authentication is null
        }

        // Extract the username from the Authentication object
        String username = authentication.getName();

        // Log the extracted username
        logger.debug("Authenticated username: {}", username);

        // Find the user by the username
        User user = authService.getCurrentUser(username);

        // Return the user details
        return ResponseEntity.ok(user);
    }


}
