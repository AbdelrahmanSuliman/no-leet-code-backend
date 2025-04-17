package com.example.noleetcode.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty; // Import if needed for specific mapping

// Ignore fields in the JSON that are not defined in this record (like 'token', 'message')
@JsonIgnoreProperties(ignoreUnknown = true)
public record Judge0Response(
        String stdout,
        String stderr,
        @JsonProperty("compile_output")
        String compileOutput,
        Judge0Status status,
        String time,
        Integer memory
) {

    // Nested record to represent the "status" object in the JSON
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Judge0Status(
            Integer id,
            String description
    ) {}
}