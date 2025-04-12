package com.example.noleetcode.Responses;

import com.example.noleetcode.enums.Language;
import com.example.noleetcode.enums.SubmissionStatus;

import java.util.UUID;

public record SubmissionResponse(UUID uuid, Language language, SubmissionStatus submissionStatus,String failureReason, Long timeTaken, Long memoryUsed) {
}
