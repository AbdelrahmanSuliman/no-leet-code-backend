package com.example.noleetcode.services;

import com.example.noleetcode.repositories.UserProblemRepository;
import com.example.noleetcode.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserProblemRepository userProblemRepository;

    public UserRepository userRepository;

    public UserService(UserProblemRepository userProblemRepository, UserRepository userRepository) {
        this.userProblemRepository = userProblemRepository;
        this.userRepository = userRepository;
    }

}
