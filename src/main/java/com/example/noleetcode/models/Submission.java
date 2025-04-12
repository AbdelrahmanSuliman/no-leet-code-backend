package com.example.noleetcode.models;

import com.example.noleetcode.enums.Language;
import com.example.noleetcode.enums.SubmissionStatus;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "user_problem_id")
    private UserProblem userProblem;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(nullable = false)
    private Language language;

    @Column
    private SubmissionStatus SubmissionStatus;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column
    private Long timeTaken;

    @Column
    private Long memoryUsed;

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    @Column
    private ZonedDateTime submittedAt = ZonedDateTime.now();

    public Submission(SubmissionStatus status, Language language, UserProblem userProblem, String code, Problem problem) {
        this.SubmissionStatus = status;
        this.userProblem = userProblem;
        this.uuid = UUID.randomUUID();
        this.language = language;
        this.code = code;
        this.problem = problem;
    }

    public Submission() {
        this.uuid = UUID.randomUUID();
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public SubmissionStatus getSubmissionStatus() {
        return SubmissionStatus;
    }

    public void setSubmissionStatus(SubmissionStatus status) {
        this.SubmissionStatus = status;
    }

    public Long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Long getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(Long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public ZonedDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(ZonedDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public UserProblem getUserProblem() {
        return userProblem;
    }

    public void setUserProblem(UserProblem userProblem) {
        this.userProblem = userProblem;
    }
}
