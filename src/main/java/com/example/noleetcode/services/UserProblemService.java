package com.example.noleetcode.services;

import com.example.noleetcode.Responses.SubmissionResponse;
import com.example.noleetcode.dto.CreateSubmissionDto;
import com.example.noleetcode.enums.SubmissionStatus;
import com.example.noleetcode.enums.UserProblemStatus;
import com.example.noleetcode.exception.ApplicationException;
import com.example.noleetcode.models.*;
import com.example.noleetcode.repositories.ProblemRepository;
import com.example.noleetcode.repositories.SubmissionRepository;
import com.example.noleetcode.repositories.UserProblemRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserProblemService {

    private static final Logger logger = LoggerFactory.getLogger(UserProblemService.class);


    private final UserProblemRepository userProblemRepository;
    private final ProblemRepository problemRepository;
    private final SubmissionRepository submissionRepository;
    private final Judge0Service judge0Service;

    ObjectMapper objectMapper = new ObjectMapper();

    public UserProblemService(UserProblemRepository userProblemRepository, ProblemRepository problemRepository, SubmissionRepository submissionRepository, Judge0Service judge0Service) {
        this.userProblemRepository = userProblemRepository;
        this.problemRepository = problemRepository;
        this.submissionRepository = submissionRepository;
        this.judge0Service = judge0Service;
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
                    return newUserProblem;
                });
        userProblem.incrementAttempts();
        userProblem.setLastAttemptedAt(ZonedDateTime.now());
        List<Submission> currentSubmissions = userProblem.getSubmissions();
        currentSubmissions.add(submission);
        userProblem.setSubmissions(currentSubmissions);

        if(submission.getSubmissionStatus() == SubmissionStatus.ACCEPTED){
            userProblem.setLastSubmissionStatus(UserProblemStatus.ACCEPTED);
            userProblem.setSolved(true);
        }
        else{
            userProblem.setLastSubmissionStatus(UserProblemStatus.ATTEMPTED);
        }
        userProblemRepository.save(userProblem);


    }


}


