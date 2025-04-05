package com.example.noleetcode.models;

import com.example.noleetcode.enums.Difficulty;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "problems")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Difficulty difficulty;

    @OneToMany(mappedBy = "problem")
    private List<UserProblem> userProblems;

    @ManyToMany
    @JoinTable(
            name = "problem_tags",
            joinColumns = @JoinColumn(name = "problem_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    @OneToMany(mappedBy = "problem")
    private List<TestCase> testCases;

    @Column(nullable = false)
    private String solution;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;


    @Column
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    public Problem(String title, String description, Difficulty difficulty, List<UserProblem> userProblems, List<Tag> tags, List<TestCase> testCases, String solution, User author) {
        this.title = title;
        this.uuid = UUID.randomUUID();
        this.description = description;
        this.difficulty = difficulty;
        this.userProblems = userProblems;
        this.tags = tags;
        this.testCases = testCases;
        this.solution = solution;
        this.author = author;
    }

    public Problem() {
        this.uuid = UUID.randomUUID();
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getAuthorId() {
        return author;
    }

    public void setAuthorId(User author) {
        this.author = author;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<UserProblem> getUsers() {
        return userProblems;
    }

    public void setUserProblems(List<UserProblem> userProblems) {
        this.userProblems = userProblems;
    }


    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
