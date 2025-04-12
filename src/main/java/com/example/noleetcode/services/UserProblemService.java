package com.example.noleetcode.services;

import com.example.noleetcode.Responses.Judge0Response;
import com.example.noleetcode.Responses.SubmissionResponse;
import com.example.noleetcode.dto.CreateSubmissionDto;
import com.example.noleetcode.enums.SubmissionStatus;
import com.example.noleetcode.enums.UserProblemStatus;
import com.example.noleetcode.exception.ApplicationException;
import com.example.noleetcode.models.*;
import com.example.noleetcode.repositories.ProblemRepository;
import com.example.noleetcode.repositories.SubmissionRepository;
import com.example.noleetcode.repositories.UserProblemRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserProblemService {

    private static final Logger logger = LoggerFactory.getLogger(UserProblemService.class);


    private final UserProblemRepository userProblemRepository;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final Judge0Service judge0Service;

    public UserProblemService(UserProblemRepository userProblemRepository, ProblemRepository problemRepository, SubmissionRepository submissionRepository, Judge0Service judge0Service) {
        this.userProblemRepository = userProblemRepository;
        this.problemRepository = problemRepository;
        this.submissionRepository = submissionRepository;
        this.judge0Service = judge0Service;
    }

    // Add imports if they are missing at the top of the file:
    // import com.example.noleetcode.Responses.Judge0Response;
    // import com.example.noleetcode.Responses.SubmissionResponse;
    // import com.example.noleetcode.dto.CreateSubmissionDto;
    // import com.example.noleetcode.enums.SubmissionStatus;
    // import com.example.noleetcode.enums.UserProblemStatus;
    // import com.example.noleetcode.exception.ApplicationException;
    // import com.example.noleetcode.models.*;
    // import com.example.noleetcode.repositories.ProblemRepository;
    // import com.example.noleetcode.repositories.SubmissionRepository;
    // import com.example.noleetcode.repositories.UserProblemRepository;
    // import org.slf4j.Logger;
    // import org.slf4j.LoggerFactory;
    // import org.springframework.http.HttpStatus;
    // import org.springframework.stereotype.Service;
    // import java.time.ZonedDateTime;
    // import java.util.UUID;

    /**
     * Handles the submission of code for a specific problem by a user.
     * Runs the code against test cases using Judge0 and determines the submission status.
     * Captures execution time, memory, and detailed failure reasons.
     */
    @Transactional // Ensure atomicity of database operations
    public SubmissionResponse handleSubmission(User user, UUID problemUuid, CreateSubmissionDto submissionDto) {
        logger.info("User id {} attempting submission for Problem UUID {}", user.getId(), problemUuid);

        Submission savedSubmission = null;
        UserProblem updatedUserProblem = null;
        String failureReason = null; // Stores the first failure reason encountered

        try {
            // 1. Retrieve problem
            Problem problem = problemRepository.findByUuid(problemUuid)
                    .orElseThrow(() -> {
                        logger.warn("Problem not found with UUID: {}", problemUuid);
                        return new ApplicationException("Problem not found", HttpStatus.NOT_FOUND);
                    });

            // 2. Retrieve or create UserProblem entity
            final User finalUser = user; // Need final variable for lambda
            UserProblem userProblem = userProblemRepository.findByUserAndProblem(user, problem)
                    .orElseGet(() -> {
                        logger.info("Creating new UserProblem for User id {} and Problem UUID {}", finalUser.getId(), problemUuid);
                        UserProblem newUserProblem = new UserProblem();
                        newUserProblem.setUser(finalUser);
                        newUserProblem.setProblem(problem);
                        newUserProblem.setFirstAttemptedAt(ZonedDateTime.now());
                        return newUserProblem; // Save happens below
                    });

            // 3. Update UserProblem attempts and save (before creating submission)
            userProblem.incrementAttempts();
            userProblem.updateLastAttemptedAt();
            userProblem.setLastSubmissionStatus(UserProblemStatus.ATTEMPTED); // Tentative status
            updatedUserProblem = userProblemRepository.save(userProblem); // Save UserProblem state

            // 4. Create and save initial Submission Record
            Submission submission = new Submission();
            submission.setUser(user);
            submission.setProblem(problem);
            submission.setUserProblem(updatedUserProblem);
            submission.setCode(submissionDto.code());
            submission.setLanguage(submissionDto.language());
            submission.setSubmissionStatus(SubmissionStatus.PENDING); // Initial pending status
            submission.setSubmittedAt(ZonedDateTime.now());
            // Ensure other fields like failureReason, time, memory are null initially
            submission.setFailureReason(null);
            submission.setTimeTaken(null);
            submission.setMemoryUsed(null);
            savedSubmission = submissionRepository.save(submission); // Save initial pending submission

            // 5. Process against test cases
            boolean allPassed = true;
            Double maxTimeTaken = 0.0;  // Track max time (seconds) from Judge0
            Integer maxMemoryUsed = 0; // Track max memory (KB) from Judge0

            if (problem.getTestCases() == null || problem.getTestCases().isEmpty()) {
                logger.warn("Problem {} has no test cases. Marking submission {} as ACCEPTED.", problemUuid, savedSubmission.getUuid());
                allPassed = true; // Pass if no tests defined
            } else {
                logger.info("Processing {} test cases for submission {}", problem.getTestCases().size(), savedSubmission.getUuid());
                for (TestCase testCase : problem.getTestCases()) {
                    logger.debug("Processing test case UUID {}", testCase.getUuid());

                    // 5a. Handle Input Formatting
                    String testInput = null;
                    if (testCase.getInput() != null && !testCase.getInput().isEmpty() && testCase.getInput().get(0) != null) {
                        // Assuming the first element is the actual input value
                        testInput = testCase.getInput().get(0).toString();
                        logger.debug("Test case {} input formatted as: '{}'", testCase.getUuid(), testInput);
                    } else {
                        logger.warn("Invalid or empty input list for test case {} in problem {}", testCase.getUuid(), problemUuid);
                        allPassed = false;
                        failureReason = "Invalid input format for test case " + testCase.getUuid();
                        break; // Stop processing
                    }

                    // 5b. Submit to Judge0
                    Judge0Response judge0Response;
                    try {
                        int languageId = judge0Service.getJudge0LanguageId(submissionDto.language());
                        logger.debug("Submitting to Judge0: lang={}, input='{}' for test case {}", languageId, testInput, testCase.getUuid());
                        judge0Response = judge0Service.submitToJudge0AndGetResult(
                                submissionDto.code(), languageId, testInput
                        );
                        // Log full Judge0 response for detailed diagnostics
                        logger.debug("Judge0 Response for test case {}: stdout='{}', stderr='{}', compile_output='{}', status_id={}, time={}, memory={}",
                                testCase.getUuid(), judge0Response.getStdout(), judge0Response.getStderr(),
                                judge0Response.getCompile_output(), judge0Response.getStatusId(),
                                judge0Response.getTime(), judge0Response.getMemory());

                        // Capture Max Time/Memory from this execution
                        if (judge0Response.getTime() != null && judge0Response.getTime() > maxTimeTaken) maxTimeTaken = judge0Response.getTime();
                        if (judge0Response.getMemory() != null && judge0Response.getMemory() > maxMemoryUsed) maxMemoryUsed = judge0Response.getMemory();

                        // Check Judge0 Status ID for execution errors BEFORE checking stdout
                        // 1=Q, 2=Proc, 3=OK, 4=WA, 5=TLE, 6=CE, 7-12=RTE, 13=IE, 14=EFE
                        if (judge0Response.getStatusId() > 3) { // Any status indicating Judge0 detected an error
                            allPassed = false;
                            failureReason = "Execution Error on test case " + testCase.getUuid() + ". Status ID: " + judge0Response.getStatusId();
                            String compileOutput = judge0Response.getCompile_output();
                            String stdErr = judge0Response.getStderr();

                            if (judge0Response.getStatusId() == 6 && compileOutput != null && !compileOutput.isBlank()) { // Compilation Error
                                failureReason += ". Compile Output: " + compileOutput.trim();
                            } else if (stdErr != null && !stdErr.isBlank()) { // Runtime Error or other errors with stderr output
                                failureReason += ". Stderr: " + stdErr.trim();
                            }
                            // Add more specific checks based on status IDs if needed (e.g., Time Limit Exceeded)
                            logger.warn("Test case {} failed execution in Judge0. Reason: {}", testCase.getUuid(), failureReason);
                            break; // Stop on first execution error reported by Judge0
                        }

                    } catch (Exception e) { // Catch errors during communication with Judge0
                        logger.error("Error calling Judge0 service for test case {}: {}", testCase.getUuid(), e.getMessage(), e);
                        allPassed = false;
                        failureReason = "Error communicating with execution service for test case " + testCase.getUuid() + ". Error: " + e.getMessage();
                        break; // Stop if Judge0 call fails
                    }

                    // 5c. Compare Output (only if Judge0 didn't report an execution error via statusId)
                    String obtainedOutput = judge0Response.getStdout() != null ? judge0Response.getStdout().trim() : "";

                    if (testCase.getOutput() != null && !testCase.getOutput().isEmpty() && testCase.getOutput().get(0) != null) {
                        // Assuming the first element is the expected output value
                        String expectedOutputString = testCase.getOutput().get(0).toString();

                        if (!obtainedOutput.equals(expectedOutputString)) {
                            // --- Failure identified as Wrong Answer ---
                            logger.info("Test case {} failed. Expected: '{}', Got: '{}'", testCase.getUuid(), expectedOutputString, obtainedOutput);
                            allPassed = false;

                            // Construct detailed failure reason, including stderr/compile output if present (might explain empty output)
                            failureReason = "Wrong Answer on test case " + testCase.getUuid() + ". Expected: '" + expectedOutputString + "', Got: '" + obtainedOutput + "'";
                            String compileOutput = judge0Response.getCompile_output();
                            String stdErr = judge0Response.getStderr();

                            if (compileOutput != null && !compileOutput.isBlank()) {
                                failureReason += ". Compile Output: " + compileOutput.trim();
                                logger.warn("Wrong Answer for test case {} also had Compile Output: {}", testCase.getUuid(), compileOutput.trim());
                            } else if (stdErr != null && !stdErr.isBlank()) {
                                failureReason += ". Stderr: " + stdErr.trim();
                                logger.warn("Wrong Answer for test case {} also had Stderr: {}", testCase.getUuid(), stdErr.trim());
                            }
                            break; // Exit loop on first wrong answer
                        } else {
                            logger.info("Test case {} passed.", testCase.getUuid());
                        }
                    } else {
                        // Handle invalid test case output format
                        logger.warn("Invalid or empty output list for test case {} in problem {}", testCase.getUuid(), problemUuid);
                        allPassed = false;
                        failureReason = "Invalid output format for test case " + testCase.getUuid();
                        break; // Stop processing
                    }
                } // End for loop
            } // End else (test cases exist)

            // 6. Finalize submission status and set metrics/reason
            if (savedSubmission == null) throw new ApplicationException("Failed to save initial submission record.", HttpStatus.INTERNAL_SERVER_ERROR);
            if (updatedUserProblem == null) throw new ApplicationException("UserProblem record was not properly initialized.", HttpStatus.INTERNAL_SERVER_ERROR);


            // Convert Judge0 time (seconds) to Long milliseconds for storage
            long timeTakenMillis = (long) (maxTimeTaken * 1000);
            // Convert Judge0 memory (KB) to Long for storage
            long memoryUsedKB = maxMemoryUsed.longValue();

            if (allPassed) {
                savedSubmission.setSubmissionStatus(SubmissionStatus.ACCEPTED);
                savedSubmission.setTimeTaken(timeTakenMillis);
                savedSubmission.setMemoryUsed(memoryUsedKB);
                savedSubmission.setFailureReason(null); // Clear reason on success

                updatedUserProblem.setLastSubmissionStatus(UserProblemStatus.ACCEPTED);
                updatedUserProblem.setSolved(true); // Mark problem as solved for the user

                logger.info("Submission {} ACCEPTED for problem {} (Time: {}ms, Memory: {}KB)", savedSubmission.getUuid(), problemUuid, timeTakenMillis, memoryUsedKB);
            } else {
                savedSubmission.setSubmissionStatus(SubmissionStatus.FAILED);
                savedSubmission.setFailureReason(failureReason); // Set the captured reason
                savedSubmission.setTimeTaken(null); // Explicitly set null on failure
                savedSubmission.setMemoryUsed(null);  // Explicitly set null on failure

                updatedUserProblem.setLastSubmissionStatus(UserProblemStatus.FAILED);
                // Keep solved as false

                logger.info("Submission {} FAILED for problem {}. Reason: {}", savedSubmission.getUuid(), problemUuid, failureReason);
            }

            // 7. Save final updates for Submission and UserProblem
            submissionRepository.save(savedSubmission);
            userProblemRepository.save(updatedUserProblem);

            // 8. Return Response (using the user's specified order)
            return new SubmissionResponse(
                    savedSubmission.getUuid(),
                    savedSubmission.getLanguage(),
                    savedSubmission.getSubmissionStatus(),
                    savedSubmission.getFailureReason(),
                    savedSubmission.getTimeTaken(),
                    savedSubmission.getMemoryUsed()
            );

        } catch (ApplicationException ae) {
            logger.error("Application error during submission for problem {}: {}", problemUuid, ae.getMessage(), ae);
            // Try to update submission status if possible
            if (savedSubmission != null) {
                try {
                    savedSubmission.setSubmissionStatus(SubmissionStatus.FAILED); // Or a specific ERROR status
                    savedSubmission.setFailureReason("Processing error: " + ae.getClass().getSimpleName() + " - " + ae.getMessage());
                    submissionRepository.save(savedSubmission);
                    // Optionally update UserProblem status too if relevant
                } catch (Exception saveEx) { logger.error("Failed to update submission status after ApplicationException: {}", saveEx.getMessage()); }
            }
            throw ae; // Re-throw specific application exception

        } catch (Exception e) { // Catch any other unexpected exceptions
            logger.error("Unexpected internal error during submission for problem {}: {}", problemUuid, e.getMessage(), e);
            // Try to update submission status if possible
            if (savedSubmission != null) {
                try {
                    savedSubmission.setSubmissionStatus(SubmissionStatus.FAILED); // Or a specific ERROR status
                    savedSubmission.setFailureReason("Internal processing error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    submissionRepository.save(savedSubmission);
                    // Optionally update UserProblem status too if relevant
                } catch (Exception saveEx) { logger.error("Failed to update submission status after generic Exception: {}", saveEx.getMessage()); }
            }
            // Throw a generic internal server error exception
            throw new ApplicationException("Unexpected internal error while processing submission.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}


