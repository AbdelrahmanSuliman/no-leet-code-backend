package com.example.noleetcode.services;

import com.example.noleetcode.config.JwtService;
import com.example.noleetcode.config.SecurityConfig;
import com.example.noleetcode.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;


    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
