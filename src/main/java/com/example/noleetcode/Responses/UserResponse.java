package com.example.noleetcode.Responses;

import com.example.noleetcode.models.Problem;
import com.example.noleetcode.models.Submission;
import com.example.noleetcode.models.User;
import com.example.noleetcode.models.UserProblem;

import java.util.List;

public record UserResponse(String username, String email, List<Submission> submissions, List<UserProblem> userProblems) {
    public UserResponse(User user) {
        this(
                user.getUsername(),
                user.getEmail(),
                user.getSubmissions(),
                user.getUserProblems()
        );
    }
}
