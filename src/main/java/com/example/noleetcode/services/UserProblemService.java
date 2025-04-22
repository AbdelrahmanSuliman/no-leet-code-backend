package com.example.noleetcode.services;

import com.example.noleetcode.Responses.SubmissionResponse;
import com.example.noleetcode.Responses.UserProblemResponse;
import com.example.noleetcode.enums.SubmissionStatus;
import com.example.noleetcode.enums.UserProblemStatus;
import com.example.noleetcode.exception.ApplicationException;
import com.example.noleetcode.models.Problem;
import com.example.noleetcode.models.Submission;
import com.example.noleetcode.models.User;
import com.example.noleetcode.models.UserProblem;
import com.example.noleetcode.repositories.ProblemRepository;
import com.example.noleetcode.repositories.SubmissionRepository;
import com.example.noleetcode.repositories.UserProblemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserProblemService {

    private static final Logger logger = LoggerFactory.getLogger(UserProblemService.class);


    private final UserProblemRepository userProblemRepository;
    private final ProblemRepository problemRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    public UserProblemService(UserProblemRepository userProblemRepository, ProblemRepository problemRepository, SubmissionRepository submissionRepository, Judge0Service judge0Service) {
        this.userProblemRepository = userProblemRepository;
        this.problemRepository = problemRepository;
    }

    public void addSubmissionToUserProblem(User user, UUID problemUuid, Submission submission) {
        Problem problem = problemRepository.findByUuid(problemUuid)
                .orElseThrow(() -> new ApplicationException("Problem not found", HttpStatus.NOT_FOUND));

        UserProblem userProblem = userProblemRepository.findByUserAndProblem(user, problem)
                .orElseGet(() -> {
                    logger.info("Creating new UserProblem record for User UUID {} and Problem UUID {}", user.getUuid(), problem.getUuid());
                    UserProblem newUserProblem = new UserProblem();
                    newUserProblem.setUser(user);
                    newUserProblem.setProblem(problem);
                    newUserProblem.setFirstAttemptedAt(ZonedDateTime.now());
                    return newUserProblem;
                });
        submission.setUserProblem(userProblem);
        userProblem.incrementAttempts();
        userProblem.setLastAttemptedAt(ZonedDateTime.now());

        if (userProblem.getSubmissions() == null) {
            logger.warn("Submissions collection was null for UserProblem ID {}. Initializing.", userProblem.getId());
            userProblem.setSubmissions(new ArrayList<>());
        }

        userProblem.getSubmissions().add(submission);


        if (submission.getSubmissionStatus() == SubmissionStatus.ACCEPTED) {
            userProblem.setLastSubmissionStatus(UserProblemStatus.ACCEPTED);
            userProblem.setSolved(true);
        }
        userProblemRepository.save(userProblem);

    }

    @Transactional
    public List<SubmissionResponse> getSubmissionsForUserAndProblem(User user, UUID problemUuid) {
        Problem problem = problemRepository.findByUuid(problemUuid)
                .orElseThrow(() -> new ApplicationException("Problem not found", HttpStatus.NOT_FOUND));

        return userProblemRepository.findByUserAndProblem(user, problem)
                .map(userProblem -> { // If UserProblem exists
                    logger.info("Found UserProblem record for User UUID {} and Problem UUID {}. Mapping submissions.", user.getUuid(), problem.getUuid());
                    return userProblem.getSubmissions().stream()
                            .map(sub -> new SubmissionResponse(
                                    sub.getUuid(),
                                    sub.getLanguage(),
                                    sub.getSubmissionStatus(),
                                    sub.getFailureReason(),
                                    sub.getTimeTaken(),
                                    sub.getMemoryUsed()
                            ))
                            .collect(Collectors.toList()); // Collect the stream of SubmissionResponse into a List
                })
                .orElseGet(() -> {
                    logger.info("No UserProblem found for User UUID {} and Problem UUID {}. Returning empty list.", user.getUuid(), problemUuid);
                    return Collections.emptyList();
                });
    }

    public List<UserProblemResponse> getUserProblemsForUser(User user) {
        return userProblemRepository.findAllByUser(user).stream()
                .map(UserProblemResponse::fromUserProblem)
                .collect(Collectors.toList());
    }
}