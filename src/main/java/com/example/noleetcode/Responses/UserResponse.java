package com.example.noleetcode.Responses;

import com.example.noleetcode.enums.Role;

public record UserResponse(
        String username,
        String email,
        Role role
) {}
