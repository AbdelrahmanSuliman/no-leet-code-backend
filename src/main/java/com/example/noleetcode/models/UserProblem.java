package com.example.noleetcode.models;

import com.example.noleetcode.enums.UserProblemStatus;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "user_problems")
public class UserProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(nullable = false)
    private boolean solved = false;

    @Column
    private Integer numberOfAttempts = 0;

    @Column
    private UserProblemStatus lastSubmissionStatus;

    @Column
    private ZonedDateTime firstAttemptedAt;

    @Column
    private ZonedDateTime lastAttemptedAt;

    @OneToMany(mappedBy = "userProblem", cascade = CascadeType.ALL)
    private List<Submission> submissions;

    public UserProblem() {}

    public UserProblem(List<Submission> submissions, UserProblemStatus lastSubmissionStatus, Integer numberOfAttempts, boolean solved, Problem problem, User user) {
        this.submissions = submissions;
        this.lastSubmissionStatus = lastSubmissionStatus;
        this.numberOfAttempts = numberOfAttempts;
        this.solved = solved;
        this.problem = problem;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public Integer getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public void setNumberOfAttempts(Integer numberOfAttempts) {
        this.numberOfAttempts = numberOfAttempts;
    }

    public UserProblemStatus getLastSubmissionStatus() {
        return lastSubmissionStatus;
    }

    public void setLastSubmissionStatus(UserProblemStatus lastSubmissionStatus) {
        this.lastSubmissionStatus = lastSubmissionStatus;
    }

    public ZonedDateTime getLastAttemptedAt() {
        return lastAttemptedAt;
    }

    public void setLastAttemptedAt(ZonedDateTime lastAttemptedAt) {
        this.lastAttemptedAt = lastAttemptedAt;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public ZonedDateTime getFirstAttemptedAt() {
        return firstAttemptedAt;
    }

    public void setFirstAttemptedAt(ZonedDateTime firstAttemptedAt) {
        this.firstAttemptedAt = firstAttemptedAt;
    }

    public void incrementAttempts() {
        this.numberOfAttempts = (this.numberOfAttempts == null ? 1 : this.numberOfAttempts + 1);
    }

    public void updateLastAttemptedAt() {
        this.lastAttemptedAt = ZonedDateTime.now();
        if (this.firstAttemptedAt == null) {
            this.firstAttemptedAt = this.lastAttemptedAt;
        }
    }
}
