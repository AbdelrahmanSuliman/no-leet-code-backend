package com.example.noleetcode.services;

import com.example.noleetcode.config.JwtService;
import com.example.noleetcode.config.SecurityConfig;
import com.example.noleetcode.dto.RegisterUserDto;
import com.example.noleetcode.exception.ApplicationException;
import com.example.noleetcode.models.Problem;
import com.example.noleetcode.models.Submission;
import com.example.noleetcode.models.User;
import com.example.noleetcode.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;
    private final JwtService jwtService;


    public AuthService(UserRepository userRepository, SecurityConfig securityConfig, JwtService jwtService) {
        this.userRepository = userRepository;
        this.securityConfig = securityConfig;
        this.jwtService = jwtService;
    }

    public String register(RegisterUserDto registerUserDto) {
        if(userRepository.findByUsername(registerUserDto.username()).isPresent()) {
            logger.warn("Username {} is already in use", registerUserDto.username());
            throw new ApplicationException("Username already in use", HttpStatus.CONFLICT);
        }


        // Save the user
        User user = new User(
                registerUserDto.username(),
                registerUserDto.email(),
                registerUserDto.password()

        );
        userRepository.save(user);

        // Generate JWT token
        String token = jwtService.generateToken(user);

        // Return the token in the response
        return token; // AuthResponse is a custom class to send the token back
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));
    }


}
