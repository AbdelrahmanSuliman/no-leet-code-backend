package com.example.noleetcode.controllers;

import com.example.noleetcode.Responses.ProblemResponse;
import com.example.noleetcode.dto.CreateProblemDto;
import com.example.noleetcode.exception.ApplicationException;
import com.example.noleetcode.models.Problem;
import com.example.noleetcode.models.User;
import com.example.noleetcode.services.ProblemService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/problem")
public class ProblemController {

    private static final Logger logger = LoggerFactory.getLogger(ProblemController.class);

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }


    @GetMapping
    public List<ProblemResponse> getProblems(@RequestParam(defaultValue = "0") int offset,
                                             @RequestParam(defaultValue = "10") int count) {
        return problemService.getProblemsPaginated(offset, count);
    }

    @PostMapping("/new")
    public ResponseEntity<?> addProblem(@RequestBody CreateProblemDto createProblemDto,
                                        @AuthenticationPrincipal User user) {

        logger.info("Adding problem: {}", createProblemDto);
        UUID problemUuid = problemService.addProblem(createProblemDto, user);
        return ResponseEntity.ok(problemUuid);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteProblem(@PathVariable UUID uuid, @AuthenticationPrincipal User user) {
        logger.info("Deleting problem: {}", uuid);
        problemService.deleteProblem(uuid);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ProblemResponse> updateProblem(@PathVariable UUID uuid){
        logger.info("Updating problem: {}", uuid);
        problemService.updateProblem(uuid);
        return ResponseEntity.ok().build();
    }


}
