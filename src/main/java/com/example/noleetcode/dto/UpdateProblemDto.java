package com.example.noleetcode.dto;

import com.example.noleetcode.enums.Difficulty;
import com.example.noleetcode.enums.TagType;
import com.example.noleetcode.models.Tag;
import com.example.noleetcode.models.TestCase;

import java.util.List;

public record UpdateProblemDto(
        String title,
        String description,
        Difficulty difficulty,
        List<TagType> tags,
        List<CreateTestCaseDto> testCases,
        String solution) {
}
