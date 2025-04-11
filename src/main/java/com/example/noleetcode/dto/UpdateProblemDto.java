package com.example.noleetcode.dto;

import com.example.noleetcode.enums.Difficulty;
import com.example.noleetcode.models.Tag;
import com.example.noleetcode.models.TestCase;

import java.util.List;

public record UpdateProblemDto(
        String title,
        String description,
        Difficulty difficulty,
        List<Tag> tags,
        List<TestCase> testCases,
        String solution) {
}
