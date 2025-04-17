package com.example.noleetcode.enums;

public enum SubmissionStatus {
    PENDING,             // Judge0 ID 1
    RUNNING,             // Judge0 ID 2
    ACCEPTED,            // Judge0 ID 3
    WRONG_ANSWER,        // Judge0 ID 4
    TIME_LIMIT_EXCEEDED, // Judge0 ID 5 (Renamed from TIMEOUT for consistency)
    COMPILATION_ERROR,   // Judge0 ID 6
    RUNTIME_ERROR,       // Covers Judge0 IDs 7, 9, 10, 11, 12, 14 (Common Runtime Issues)
    OUTPUT_LIMIT_EXCEEDED, // Judge0 ID 8
    MEMORY_LIMIT_EXCEEDED, // If needed, often maps to Runtime Error in Judge0
    INTERNAL_ERROR;      // Judge0 ID 13 (System Issue)
}