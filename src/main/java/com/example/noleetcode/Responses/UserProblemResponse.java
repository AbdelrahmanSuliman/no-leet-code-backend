package com.example.noleetcode.Responses;

import com.example.noleetcode.enums.UserProblemStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

public record UserProblemResponse(UUID userUuid, boolean solved, Integer numberOfAttempts, UserProblemStatus lastSubmissionStatus, ZonedDateTime lastAttemptedAt) {
    public static UserProblemResponse fromUserProblem(com.example.noleetcode.models.UserProblem up) {
        UUID userId = (up.getUser() != null) ? up.getUser().getUuid() : null;
        return new UserProblemResponse(
                userId,
                up.isSolved(),
                up.getNumberOfAttempts(),
                up.getLastSubmissionStatus(),
                up.getLastAttemptedAt()
        );
    }
}