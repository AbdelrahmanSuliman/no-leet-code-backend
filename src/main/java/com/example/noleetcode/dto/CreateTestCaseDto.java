    package com.example.noleetcode.dto;

    import java.util.List;

    public record CreateTestCaseDto(
             List<Object> input,
             List<Object> output
    ) {}