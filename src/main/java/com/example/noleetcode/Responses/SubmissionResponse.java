package com.example.noleetcode.Responses;

import com.example.noleetcode.enums.Language;
import com.example.noleetcode.enums.SubmissionStatus;

import java.util.UUID;

public record SubmissionResponse(UUID uuid, UserResponse user, UserProblemResponse userProblem, String code, Language language, SubmissionStatus submissionStatus, ProblemResponse problem, Long timeTaken, Long memoryUsed) {
}
