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

        authService.register(registerUserDto);
        return ResponseEntity.ok("Registration successful. Please check your email to verify your account.");
    }


    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody VerifyEmailDto verifyEmailDto) {
        String token = authService.verifyEmail(verifyEmailDto);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginUserDto loginUserDto) {
        String token = authService.login(loginUserDto);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok("If an account with that email exists, a password reset code has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordWithCodeDto resetDto) {
        authService.resetPassword(resetDto);
        return ResponseEntity.ok("Password has been successfully reset.");
    }



}
