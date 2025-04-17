package com.example.noleetcode.models;

import com.example.noleetcode.config.ObjectListConverter;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "test_cases")
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Internal ID

    @Column(unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = ObjectListConverter.class)
    private List<Object> input;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = ObjectListConverter.class)
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


    @PrePersist // Ensure UUID is set before persisting if not done elsewhere
    public void ensureUuid() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
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

    public String getFormattedInputString() {
        if (this.input == null || this.input.isEmpty()) {
            return "";
        }

        return this.input.stream()
                .map(obj -> {
                    if (obj instanceof List<?> list) {
                        // If it's a list, join its elements' string representations with spaces
                        return list.stream()
                                .map(element -> element == null ? "" : element.toString())
                                .collect(Collectors.joining(" ")); // Join elements with a single space
                    } else {
                        return obj == null ? "" : obj.toString();
                    }
                })
                .collect(Collectors.joining("\n")); // Join each processed element/line with a newline
    }

    public String getFormattedOutputString(){
        if (this.output == null || this.output.isEmpty()) {
            return "";
        }
        return this.output.stream()
                .map(obj -> {
                    if(obj instanceof List<?> list){
                        return list.stream()
                                .map(element -> element == null ? "" : element.toString())
                                .collect(Collectors.joining(" "));
                    }else{
                        return obj == null ? "" : obj.toString();
                    }

                })
                .collect(Collectors.joining(" "));
    }
}