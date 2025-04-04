package com.example.noleetcode.Responses;

import com.example.noleetcode.models.Problem;
import com.example.noleetcode.models.Submission;
import com.example.noleetcode.models.User;

import java.util.List;

public record UserResponse(String username, String email, List<Submission> submissions, List<Problem> problems) {
    public UserResponse(User user) {
        this(
                user.getUsername(),
                user.getEmail(),
                user.getSubmissions(),
                user.getProblems()
        );
    }
}
