package com.example.noleetcode.services;

import com.example.noleetcode.config.AppConfig;
import com.example.noleetcode.config.JwtService;
import com.example.noleetcode.dto.LoginUserDto;
import com.example.noleetcode.dto.RegisterUserDto;
import com.example.noleetcode.exception.ApplicationException;
import com.example.noleetcode.models.User;
import com.example.noleetcode.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;



    public AuthService(UserRepository userRepository, JwtService jwtService, AppConfig appConfig, BCryptPasswordEncoder passwordEncoder, MailService mailService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(RegisterUserDto registerUserDto) {
        if(userRepository.findByUsername(registerUserDto.username()).isPresent()) {
            logger.warn("Username {} is already in use", registerUserDto.username());
            throw new ApplicationException("Username already in use", HttpStatus.CONFLICT);
        }

        if(userRepository.findByEmail(registerUserDto.email()).isPresent()) {
            logger.warn("Email {} is already in use", registerUserDto.email());
            throw new ApplicationException("Email already in use", HttpStatus.CONFLICT);
        }

        String encryptedPassword = passwordEncoder.encode(registerUserDto.password());

        // Save the user
        User user = new User(
                registerUserDto.username(),
                registerUserDto.email(),
                encryptedPassword
        );

        userRepository.save(user);

        return jwtService.generateToken(user);
    }



    public String login(LoginUserDto loginUserDto) {
        User user = userRepository.findByUsername(loginUserDto.username())
                .orElseThrow(() -> new ApplicationException("User not found", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(loginUserDto.password(), user.getPassword())) {
            logger.warn("User with username {} has incorrect password", loginUserDto.username());
            throw new ApplicationException("Wrong password", HttpStatus.UNAUTHORIZED);
        }

        return jwtService.generateToken(user);
    }




}
