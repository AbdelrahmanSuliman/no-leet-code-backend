package com.example.noleetcode.Responses;

import java.util.UUID;

public record TestCaseResponse(UUID uuid, String input, String output, Problem problem) {
}
