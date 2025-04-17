package com.example.noleetcode.controllers;

import com.example.noleetcode.dto.LoginUserDto;
import com.example.noleetcode.dto.RegisterUserDto;
import com.example.noleetcode.dto.ResetPasswordWithCodeDto;
import com.example.noleetcode.dto.VerifyEmailDto;
import com.example.noleetcode.services.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterUserDto registerUserDto) {

        String token = authService.register(registerUserDto);
        return ResponseEntity.ok(token);
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginUserDto loginUserDto) {
        String token = authService.login(loginUserDto);
        return ResponseEntity.ok(token);
    }



}
