package com.example.noleetcode.dto;

import com.example.noleetcode.enums.Difficulty;
import com.example.noleetcode.enums.TagType;
import com.example.noleetcode.models.Tag;
import com.example.noleetcode.models.TestCase;
import com.example.noleetcode.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateProblemDto(
        String title,
        String description,
        Difficulty difficulty,
        List<TagType> tags,
        List<CreateTestCaseDto> testCases,
        String solution) {
}
