package com.example.noleetcode.controllers;

import com.example.noleetcode.Responses.SubmissionResponse;
import com.example.noleetcode.dto.CreateSubmissionDto;
import com.example.noleetcode.models.User;
import com.example.noleetcode.services.UserProblemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/submission")
public class SubmissionController {
    private final UserProblemService userProblemService;

    public SubmissionController(UserProblemService userProblemService) {
        this.userProblemService = userProblemService;
    }

    @PostMapping("/submit")
    public ResponseEntity<SubmissionResponse> submitToProblem(
            @AuthenticationPrincipal User User,
            @RequestParam UUID problemUuid,
            @RequestBody CreateSubmissionDto submissionDto) {

        try {
            SubmissionResponse submissionResponse = userProblemService.handleSubmission(User, problemUuid, submissionDto);
            return ResponseEntity.ok(submissionResponse);  // Return a success response with submission details.
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);  // Handle errors and return an appropriate error message.
        }
    }
}
