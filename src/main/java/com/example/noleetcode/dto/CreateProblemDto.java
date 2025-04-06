package com.example.noleetcode.dto;

import com.example.noleetcode.enums.Difficulty;
import com.example.noleetcode.models.Tag;
import com.example.noleetcode.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateProblemDto(
        String title,
        String description,
        Difficulty difficulty,
        List<Tag> tags,
        String solution) {
}
