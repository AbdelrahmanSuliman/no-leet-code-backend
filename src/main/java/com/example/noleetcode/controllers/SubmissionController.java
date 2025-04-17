package com.example.noleetcode.controllers;

import com.example.noleetcode.Responses.SubmissionResponse;
import com.example.noleetcode.dto.CreateSubmissionDto;
import com.example.noleetcode.models.User;
import com.example.noleetcode.services.SubmissionService;
import com.example.noleetcode.services.UserProblemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/submission")
public class SubmissionController {
    private final UserProblemService userProblemService;
    private final SubmissionService submissionService;

    public SubmissionController(UserProblemService userProblemService, SubmissionService submissionService) {
        this.userProblemService = userProblemService;
        this.submissionService = submissionService;
    }

    @PostMapping("/submit")
    public ResponseEntity<SubmissionResponse> submitToProblem(
            @AuthenticationPrincipal User user,
            @RequestParam UUID problemUuid,
            @RequestBody CreateSubmissionDto submissionDto) {

        SubmissionResponse submissionResponse = submissionService.processSubmission(user, problemUuid, submissionDto);
        return ResponseEntity.ok(submissionResponse);
    }
}
