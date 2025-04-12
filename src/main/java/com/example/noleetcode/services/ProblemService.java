package com.example.noleetcode.services;

import com.example.noleetcode.Responses.*;
import com.example.noleetcode.dto.UpdateProblemDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.example.noleetcode.config.JwtService;
import com.example.noleetcode.dto.CreateProblemDto;
import com.example.noleetcode.enums.TagType;
import com.example.noleetcode.exception.ApplicationException;
import com.example.noleetcode.models.Problem;
import com.example.noleetcode.models.Tag;
import com.example.noleetcode.models.TestCase;
import com.example.noleetcode.models.User;
import com.example.noleetcode.repositories.ProblemRepository;
import com.example.noleetcode.repositories.TagRepository;
import com.example.noleetcode.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProblemService {
    private static final Logger logger = LoggerFactory.getLogger(ProblemService.class);

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ProblemRepository problemRepository;
    private final TagRepository tagRepository;
    public ProblemService(UserRepository userRepository, JwtService jwtService, ProblemRepository problemRepository, TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.problemRepository = problemRepository;
        this.tagRepository = tagRepository;
    }

    public List<ProblemSummaryResponse> getProblemSummariesPaginated(int offset, int count) { // Return summary list
        logger.info("Getting problems summary offset: {}, count: {}", offset, count);
        PageRequest pageRequest = PageRequest.of(offset, count, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Problem> problemPage = problemRepository.findAll(pageRequest);

        List<ProblemSummaryResponse> responses = problemPage.getContent().stream()
                .map(ProblemSummaryResponse::fromProblem) // Use the mapping method
                .toList();

        logger.debug("Fetched {} problem summaries", responses.size());
        return responses; // Return the list of summaries
    }

    public ProblemResponse getProblemDetailsByUuid(UUID uuid) {
        logger.info("Getting details for problem UUID: {}", uuid);
        Problem problem = problemRepository.findByUuid(uuid)
                .orElseThrow(() -> {
                    logger.warn("Problem not found with UUID: {}", uuid);
                    return new ApplicationException("Problem not found", HttpStatus.NOT_FOUND);
                });

        // --- Map Tags ---
        Set<TagResponse> tagResponses = problem.getTags().stream()
                .map(tag -> new TagResponse(tag.getTagType()))
                .collect(Collectors.toSet());

        // --- Map TestCases ---
        List<TestCaseResponse> testCaseResponses = problem.getTestCases().stream()
                .map(TestCaseResponse::fromTestCase) // Use factory method or: new TestCaseResponse(tc.getUuid(), tc.getInput(), tc.getOutput())
                .collect(Collectors.toList());

        // --- Map UserProblems ---
        List<UserProblemResponse> userProblemResponses = problem.getUserProblems().stream()
                .filter(up -> up.getUser() != null) // Optional: safety check
                .map(UserProblemResponse::fromUserProblem) // Use factory method or explicit constructor
                .collect(Collectors.toList());

        return new ProblemResponse(
                problem.getUuid(),
                problem.getTitle(),
                problem.getDescription(),
                problem.getDifficulty(),
                userProblemResponses,     // <-- Use mapped DTO list
                tagResponses,             // <-- Use mapped DTO set
                testCaseResponses,        // <-- Use mapped DTO list
                problem.getSolution(),
                problem.getCreatedAt(),
                problem.getUpdatedAt()
        );
    }

    @Transactional
    public UUID addProblem(CreateProblemDto createProblemDto, User author) {
        logger.info("Adding problem: {}", createProblemDto);

        // --- Start: Validation checks (keep these as they are) ---
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
        if (createProblemDto.solution() == null) { // Check for null solution
            throw new ApplicationException("Solution cannot be empty", HttpStatus.BAD_REQUEST);
        }


        Set<Tag> tags = new HashSet<>(); // Use Set for ManyToMany relationship
        for (TagType tagType : createProblemDto.tags()) {
            Tag tag = tagRepository.findByTagType(tagType)
                    .orElseThrow(() -> new ApplicationException("Tag does not exist: " + tagType, HttpStatus.NOT_FOUND));
            tags.add(tag);
        }



        // --- Create Problem entity (without test cases initially) ---
        Problem problem = new Problem();
        problem.setTitle(createProblemDto.title());
        problem.setDescription(createProblemDto.description());
        problem.setDifficulty(createProblemDto.difficulty());
        problem.setSolution(createProblemDto.solution());
        problem.setAuthor(author);
        problem.setTags(tags);

        // --- End: Create Problem entity ---


        // --- Process test cases and set the relationship ---
        if (createProblemDto.testCases() == null || createProblemDto.testCases().isEmpty()) {
            throw new ApplicationException("Test cases cannot be empty", HttpStatus.BAD_REQUEST);
        }

        List<TestCase> testCases = createProblemDto.testCases().stream()
                .map(testCaseDto -> {
                    TestCase testCase = new TestCase();
                    testCase.setInput(testCaseDto.input());
                    testCase.setOutput(testCaseDto.output());
                    testCase.setProblem(problem); // *** Set the owning Problem instance here ***
                    return testCase;
                })
                .collect(Collectors.toList()); // Collect to List
        // --- End: Process test cases ---


        // --- Set the test cases on the problem ---
        problem.setTestCases(testCases);
        // --- End: Set the test cases ---


        // --- Save the problem (cascade will save test cases with correct problem_id) ---
        Problem savedProblem = problemRepository.save(problem);
        logger.info("Problem saved with UUID: {}", savedProblem.getUuid());
        // --- End: Save the problem ---

        return savedProblem.getUuid();
    }


    public void deleteProblem(UUID problemId) {
        logger.info("Deleting problem: {}", problemId);

        Problem problem = problemRepository.findByUuid(problemId)
                        .orElseThrow(() -> new ApplicationException("Problem not found", HttpStatus.NOT_FOUND));

        problemRepository.delete(problem);
    }

    public void updateProblem(UUID problemId, UpdateProblemDto updatedProblem){
        logger.info("Updating problem: {}", problemId);
        Problem problem = problemRepository.findByUuid(problemId)
                .orElseThrow(() -> new ApplicationException("Problem not found", HttpStatus.NOT_FOUND));

        List<TestCase> testCases = updatedProblem.testCases().stream()
                .map(testCaseDto -> {
                    TestCase testCase = new TestCase();
                    testCase.setInput(testCaseDto.input());
                    testCase.setOutput(testCaseDto.output());
                    testCase.setProblem(problem);
                    return testCase;
                })
                .toList();

        Set<Tag> tags = new HashSet<>();
        for (TagType tagType : updatedProblem.tags()) {
            Tag tag = tagRepository.findByTagType(tagType)
                    .orElseThrow(() -> new ApplicationException("Tag does not exist: " + tagType, HttpStatus.NOT_FOUND));
            tags.add(tag);
        }
        problem.setTitle(updatedProblem.title());
        problem.setDescription(updatedProblem.description());
        problem.setDifficulty(updatedProblem.difficulty());
        problem.setTags(tags);
        problem.setTestCases(testCases);
        problem.setSolution(updatedProblem.solution());
        problem.setUpdatedAt(ZonedDateTime.now());
        problemRepository.save(problem);

    }


}
