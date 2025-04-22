package com.example.noleetcode.services;

import com.example.noleetcode.Responses.Judge0Response;
import com.example.noleetcode.Responses.SubmissionResponse;
import com.example.noleetcode.dto.CreateSubmissionDto;
import com.example.noleetcode.enums.SubmissionStatus;
import com.example.noleetcode.models.Problem;
import com.example.noleetcode.models.Submission;
import com.example.noleetcode.models.TestCase;
import com.example.noleetcode.models.User;
import com.example.noleetcode.repositories.ProblemRepository;
import com.example.noleetcode.repositories.SubmissionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    private final ProblemRepository problemRepository;
    private final Judge0Service judge0Service;

    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);
    private final SubmissionRepository submissionRepository;
    private final UserProblemService userProblemService;


    public SubmissionService(ProblemRepository problemRepository, Judge0Service judge0Service, SubmissionRepository submissionRepository, UserProblemService userProblemService) {
        this.problemRepository = problemRepository;
        this.judge0Service = judge0Service;
        this.submissionRepository = submissionRepository;
        this.userProblemService = userProblemService;
    }


    public SubmissionResponse processSubmission(User user, UUID problemUuid, CreateSubmissionDto submissionDto) {
        Submission submission = new Submission();
        submission.setUser(user);
        submission.setCode(submissionDto.code());
        submission.setLanguage(submissionDto.language());
        submission.setSubmittedAt(ZonedDateTime.now());


        Problem problem = problemRepository.findByUuid(problemUuid)
                .orElseThrow(() ->
                        new RuntimeException("Problem not found"));

        submission.setProblem(problem);


        int languageId = judge0Service.getJudge0LanguageId(submission.getLanguage());

        List<TestCase> testCases = problem.getTestCases();
        List<Judge0Response> results = new ArrayList<>();

        logger.info("Submitting code for problem {} (UUID: {}) for user {} (UUID: {})",
                problem.getTitle(), problemUuid, user.getUsername(), user.getUuid());

            try {
                for( TestCase tc : testCases) {
                    logger.debug("Running test case UUID: {}", tc.getUuid());
                    //TODO: wrap submission.getCode() in some sort of boilerplate code according to the language
                    results.add(judge0Service.submitToJudge0AndGetResult(submission.getCode(), languageId,tc.getFormattedInputString()));
                }
            } catch (IOException | InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Failed to submit to Judge0 due to I/O issue", e);
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error during Judge0 submission", e);
            }
        logger.info("Received {} results from Judge0 for problem UUID {}", results.size(), problemUuid);

        SubmissionStatus finalStatus = SubmissionStatus.ACCEPTED;
        String failureReason = "";
        long maxTime = 0L;
        long maxMemory = 0L;
        int testCaseIndex = 0;

        for(Judge0Response result : results) {
            testCaseIndex++;
            logger.debug("Evaluating result for test case index{}: {}", testCaseIndex - 1, result);
            if (result.time() != null) {
                try {
                    maxTime = Math.max(maxTime, (long)(Double.parseDouble(result.time()) * 1000)); // Parse String time
                } catch (NumberFormatException e) {
                    logger.warn("Could not parse time '{}' for test case {}", result.time(), testCaseIndex);
                }

            }
            if (result.memory() != null) {
                maxMemory = Math.max(maxMemory, result.memory());
            }

            //Handles compilation and error outputs
            if(result.status() == null || result.status().id() == null || result.status().id() != 3) {
                int statusId = (result.status() != null && result.status().id() != null) ? result.status().id() : -1; // Default ID if null
                finalStatus = judge0Service.mapJudge0Status(statusId); // Map the Judge0 status ID

                if (result.compileOutput() != null && !result.compileOutput().isBlank()) {
                    failureReason += ". Compile Output: " + result.compileOutput().lines().limit(5).collect(Collectors.joining("\\n")); // Limit output
                } else if (result.stderr() != null && !result.stderr().isBlank()) {
                    failureReason += ". Error Output: " + result.stderr().lines().limit(5).collect(Collectors.joining("\\n")); // Limit output
                }
                return new SubmissionResponse(
                        submission.getUuid(),
                        submission.getLanguage(),
                        finalStatus, // The mapped failure status
                        failureReason, // The constructed reason
                        maxTime, // Time accumulated so far (or time of a failing case)
                        maxMemory // Memory accumulated so far (or memory of a failing case)
                );
            }
            TestCase currentTestCase = testCases.get(testCaseIndex - 1);
            String actualOutput = (result.stdout() != null) ? result.stdout().trim() : "";
            String expectedOutput = currentTestCase.getFormattedOutputString();

            logger.debug("Comparing outputs for test case index {}:\nExpected: '{}'\nActual:   '{}'", testCaseIndex - 1, expectedOutput, actualOutput);

            //Handles Wrong Answers
            if(!actualOutput.equals(expectedOutput)){
                finalStatus = SubmissionStatus.WRONG_ANSWER;
                failureReason = String.format("Test case %d failed: Wrong Answer", testCaseIndex);
                logger.warn("Submission failed on test case {}. Reason: Wrong Answer. Expected: '{}', Got: '{}'", testCaseIndex, expectedOutput, actualOutput);
            }
            logger.debug("Test case index {} passed.", testCaseIndex - 1);


        }
        logger.info("All {} test cases passed for submission UUID {}", testCases.size(), submission.getUuid());

        submission.setSubmissionStatus(finalStatus);
        submission.setFailureReason(failureReason);
        submission.setTimeTaken(maxTime);
        submission.setMemoryUsed(maxMemory);

        submissionRepository.save(submission);
        logger.info("Saved final submission record with UUID: {}", submission.getUuid());

        userProblemService.addSubmissionToUserProblem(user, problemUuid, submission);

        //All test cases passed so no failure reason and accepted by default
        return new SubmissionResponse(
                submission.getUuid(),
                submission.getLanguage(),
                finalStatus, // Accepted by default
                null, // No failure reason
                maxTime, // Max time across all accepted cases
                maxMemory // Max memory across all accepted cases
        );
    }
}
