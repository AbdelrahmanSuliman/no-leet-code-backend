package com.example.noleetcode.dto;

import com.example.noleetcode.enums.Language;

import java.util.UUID;

public record CreateSubmissionDto(String code, Language language) {
}
