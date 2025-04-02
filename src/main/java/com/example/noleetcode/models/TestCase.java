package com.example.noleetcode.models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "test_cases")
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Internal ID

    @Column(unique = true, nullable = false)
    private UUID uuid;

    @Column(nullable = false)
    private String input;

    @Column(nullable = false)
    private String output;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    public TestCase(String output, Problem problem) {
        this.output = output;
        this.problem = problem;
    }
    public TestCase() {}

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

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }
}
