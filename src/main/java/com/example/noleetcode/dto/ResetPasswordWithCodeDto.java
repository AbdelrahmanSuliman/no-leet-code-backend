package com.example.noleetcode.dto;

public record ResetPasswordWithCodeDto(String email, String code, String newPassword) {}
