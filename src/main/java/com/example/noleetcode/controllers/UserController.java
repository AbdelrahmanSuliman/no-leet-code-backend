package com.example.noleetcode.controllers;

import com.example.noleetcode.Responses.SubmissionResponse;
import com.example.noleetcode.Responses.UserProblemResponse;
import com.example.noleetcode.Responses.UserResponse;
import com.example.noleetcode.models.User;
import com.example.noleetcode.services.UserProblemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserProblemService userProblemService;
    public UserController(UserProblemService userProblemService) {
        this.userProblemService = userProblemService;
    }
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(new UserResponse(user.getUsername(), user.getEmail(), user.getRole()));
    }

    @GetMapping("/me/problems")
    public ResponseEntity<List<UserProblemResponse>> getUserProblems(
            @AuthenticationPrincipal User user) {
        logger.info("Getting user problems for user {}", user.getUsername());
        List<UserProblemResponse> userProblems = userProblemService.getUserProblemsForUser(user);
        return ResponseEntity.ok(userProblems);
    }

    @GetMapping("/me/problems/{problemUuid}/submissions")
    public ResponseEntity<List<SubmissionResponse>> getUserSubmissionsForProblem(
            @AuthenticationPrincipal User user,
            @PathVariable UUID problemUuid) {
        logger.info("Getting user submissions for user {} and problem {}", user.getUsername(), problemUuid);
        List<SubmissionResponse> submissions = userProblemService.getSubmissionsForUserAndProblem(user, problemUuid);
        return ResponseEntity.ok(submissions);
    }
}
