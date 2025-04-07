package com.example.noleetcode.services;

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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Arrays;
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
                createProblemDto.solution(),
                author
        );

        problemRepository.save(problem);

        return problem.getUuid();
    }


}
