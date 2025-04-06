package com.example.noleetcode.dto;

import com.example.noleetcode.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProblemDto(
        String title,
        String description) {
}
