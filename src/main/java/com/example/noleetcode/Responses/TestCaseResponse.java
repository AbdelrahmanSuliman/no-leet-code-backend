package com.example.noleetcode.Responses;

import java.util.List;
import java.util.UUID;

public record TestCaseResponse(UUID uuid, List<Object> input, List<Object> output) {
    public static TestCaseResponse fromTestCase(com.example.noleetcode.models.TestCase tc) {
        return new TestCaseResponse(tc.getUuid(), tc.getInput(), tc.getOutput());
    }
}