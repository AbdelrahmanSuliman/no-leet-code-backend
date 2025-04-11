package com.example.noleetcode.models;

import com.example.noleetcode.config.ObjectListConverter;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "test_cases")
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Internal ID

    @Column(unique = true, nullable = false, updatable = false) // Added updatable = false assuming UUID is set once
    private UUID uuid;

    // Removed @ElementCollection
    @Column(nullable = false, columnDefinition = "TEXT") // Use TEXT or jsonb for PostgreSQL
    @Convert(converter = ObjectListConverter.class)  // Keep the converter
    private List<Object> input;

    // Removed @ElementCollection
    @Column(nullable = false, columnDefinition = "TEXT") // Use TEXT or jsonb for PostgreSQL
    @Convert(converter = ObjectListConverter.class)  // Keep the converter
    private List<Object> output;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    // Ensure a default constructor and potentially one setting the UUID
    public TestCase() {
        this.uuid = UUID.randomUUID(); // Initialize UUID here or ensure it's set elsewhere
    }
    public TestCase(List<Object> input, List<Object> output) {
        this(); // Call default constructor to set UUID
        this.input = input;
        this.output = output;
    }

    // Make sure problem is set when creating TestCase instance in ProblemService
    // Add pre-persist logic if needed or ensure UUID is set before saving

    @PrePersist // Ensure UUID is set before persisting if not done elsewhere
    public void ensureUuid() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
    }


    // Getters and Setters remain the same...

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

    public List<Object> getInput() {
        return input;
    }

    public void setInput(List<Object> input) {
        this.input = input;
    }

    public List<Object> getOutput() {
        return output;
    }

    public void setOutput(List<Object> output) {
        this.output = output;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }
}