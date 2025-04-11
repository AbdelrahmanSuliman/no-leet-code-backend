package com.example.noleetcode.Responses;

import com.example.noleetcode.enums.Difficulty;


import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
public record ProblemResponse(UUID uuid, String title, String description, Difficulty difficulty, List<UserProblemResponse> userProblems, Set<TagResponse> tags, List<TestCaseResponse> testCases, String solution, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
}
