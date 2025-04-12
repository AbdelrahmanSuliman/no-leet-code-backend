package com.example.noleetcode.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty; // Import if using @JsonProperty

// Keep this annotation
@JsonIgnoreProperties(ignoreUnknown = true)
public class Judge0Response {
    private String stdout;
    private String stderr;
    private String compile_output;

    // Use @JsonProperty if the JSON field name differs from your variable name,
    // e.g., if the JSON field is "status_id" but you want variable "statusId"
    @JsonProperty("status_id") // Map JSON "status_id" to this field
    private int statusId; // Consider renaming variable for convention

    // --- Add fields for Time and Memory ---
    private Double time; // Judge0 typically returns time in seconds (can be decimal)
    private Integer memory; // Judge0 typically returns memory in kilobytes

    // Getters and Setters for existing fields
    public String getStdout() { return stdout; }
    public void setStdout(String stdout) { this.stdout = stdout; }

    public String getStderr() { return stderr; }
    public void setStderr(String stderr) { this.stderr = stderr; }

    public String getCompile_output() { return compile_output; }
    public void setCompile_output(String compile_output) { this.compile_output = compile_output; }

    // Getter and Setter for statusId (using the renamed variable)
    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }


    // --- Getters and Setters for NEW fields ---
    public Double getTime() { return time; }
    public void setTime(Double time) { this.time = time; }

    public Integer getMemory() { return memory; }
    public void setMemory(Integer memory) { this.memory = memory; }
}