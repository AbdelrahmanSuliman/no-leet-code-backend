package com.example.noleetcode.services;

import com.example.noleetcode.Responses.ProblemResponse;
import com.example.noleetcode.config.JwtService;
import com.example.noleetcode.dto.CreateProblemDto;
import com.example.noleetcode.enums.TagType;
import com.example.noleetcode.exception.ApplicationException;
import com.example.noleetcode.models.Problem;
import com.example.noleetcode.models.Tag;
import com.example.noleetcode.models.User;
import com.example.noleetcode.repositories.ProblemRepository;
import com.example.noleetcode.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ProblemService {
    private static final Logger logger = LoggerFactory.getLogger(ProblemService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ProblemRepository problemRepository;
    public ProblemService(UserRepository userRepository, JwtService jwtService, ProblemRepository problemRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.problemRepository = problemRepository;
    }

    public List<ProblemResponse> getProblemsPaginated(int offset, int count) {
        logger.info("Getting problems offset: {}, count: {}", offset, count);
        PageRequest pageRequest = PageRequest.of(offset, count, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Problem> problems = problemRepository.findAll(pageRequest).getContent();

        List<ProblemResponse> responses = problems.stream()
                .map(problem -> new ProblemResponse(
                        problem.getUuid(),
                        problem.getTitle(),
                        problem.getDescription(),
                        problem.getDifficulty(),
                        problem.getUserProblems(),
                        problem.getTags(),
                        problem.getTestCases(),
                        problem.getSolution(),
                        problem.getCreatedAt(),
                        problem.getUpdatedAt()
                ))
                .toList();

        logger.debug("Fetched {} problems", responses.size());

        return responses;

    }

    @Transactional
    public UUID addProblem(CreateProblemDto createProblemDto, User author) {
        logger.info("Adding problem: {}", createProblemDto);

        if (createProblemDto.title() == null || createProblemDto.title().isEmpty()) {
            throw new ApplicationException("Title cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (problemRepository.existsByTitle(createProblemDto.title())) {
            throw new ApplicationException("Title already exists", HttpStatus.CONFLICT);
        }
        if (createProblemDto.description() == null || createProblemDto.description().isEmpty()) {
            throw new ApplicationException("Description cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (createProblemDto.difficulty() == null) {
            throw new ApplicationException("Difficulty cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (createProblemDto.tags() == null || createProblemDto.tags().isEmpty()) {
            throw new ApplicationException("Tags cannot be empty", HttpStatus.BAD_REQUEST);
        }
        if (createProblemDto.solution() == null) {
            throw new ApplicationException("Solution cannot be empty", HttpStatus.BAD_REQUEST);
        }


        Problem problem = new Problem(
                createProblemDto.title(),
                createProblemDto.description(),
                createProblemDto.difficulty(),
                createProblemDto.tags(),
                createProblemDto.testCases(),
                createProblemDto.solution(),
                author
        );

        problemRepository.save(problem);

        return problem.getUuid();
    }

    public void deleteProblem(UUID problemId) {
        logger.info("Deleting problem: {}", problemId);

        Problem problem = problemRepository.findByUuid(problemId)
                        .orElseThrow(() -> new ApplicationException("Problem not found", HttpStatus.NOT_FOUND));

        problemRepository.delete(problem);
    }

    public void updateProblem(UUID problemId){
        logger.info("Updating problem: {}", problemId);
        Problem problem = problemRepository.findByUuid(problemId)
                .orElseThrow(() -> new ApplicationException("Problem not found", HttpStatus.NOT_FOUND));
        problem.setTitle(problem.getTitle());
        problem.setDescription(problem.getDescription());
        problem.setDifficulty(problem.getDifficulty());
        problem.setTags(problem.getTags());
        problem.setTestCases(problem.getTestCases());
        problem.setSolution(problem.getSolution());
        problemRepository.save(problem);

    }


}
