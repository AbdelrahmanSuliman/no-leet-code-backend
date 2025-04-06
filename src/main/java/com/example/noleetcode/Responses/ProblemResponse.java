package com.example.noleetcode.Responses;

import com.example.noleetcode.enums.Difficulty;
import com.example.noleetcode.models.Tag;
import com.example.noleetcode.models.TestCase;
import com.example.noleetcode.models.UserProblem;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record ProblemResponse(UUID uuid, String title, String description, Difficulty difficulty, List<UserProblem> userProblems, List<Tag> tags, List<TestCase> testCases, String Solution, ZonedDateTime createdAt,ZonedDateTime updatedAt) {
}
