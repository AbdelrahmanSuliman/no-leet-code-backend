package com.example.noleetcode.services;

import com.example.noleetcode.models.UserProblem;
import com.example.noleetcode.repositories.UserProblemRepository;
import com.example.noleetcode.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    public UserProblemRepository userProblemRepository;

    public UserRepository userRepository;

    public List<UserProblem> getUserProblemsPaginated(){
        return null;
    }
}
