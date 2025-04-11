// In Responses package
package com.example.noleetcode.Responses;

import com.example.noleetcode.enums.Difficulty;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.example.noleetcode.models.Problem;
import com.example.noleetcode.models.Tag;


public record ProblemSummaryResponse(
        UUID uuid,
        String title,
        Difficulty difficulty,
        Set<String> tags
) {
    public static ProblemSummaryResponse fromProblem(Problem problem) {
        return new ProblemSummaryResponse(
                problem.getUuid(),
                problem.getTitle(),
                problem.getDifficulty(),
                problem.getTags().stream()
                        .map(tag -> tag.getTagType().name()) // Get name from enum
                        .collect(Collectors.toSet())
        );
    }
}