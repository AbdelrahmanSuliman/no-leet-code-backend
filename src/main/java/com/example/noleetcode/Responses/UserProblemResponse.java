package com.example.noleetcode.Responses;

import com.example.noleetcode.enums.UserProblemStatus;

import java.util.List;

public record UserProblemResponse(UserResponse userResponse, ProblemResponse problemResponse, boolean solved, Integer numberOfAttempts, UserProblemStatus lastSubmissionStatus, List<SubmissionResponse> submissions) {
}
