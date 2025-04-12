package com.example.noleetcode.Responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty; // Import if using @JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
public class Judge0Response {
    private String stdout;
    private String stderr;
    private String compile_output;

    @JsonProperty("status_id") // Map JSON "status_id" to this field
    private int statusId;

    private Double time;
    private Integer memory;

    public String getStdout() { return stdout; }
    public void setStdout(String stdout) { this.stdout = stdout; }

    public String getStderr() { return stderr; }
    public void setStderr(String stderr) { this.stderr = stderr; }

    public String getCompile_output() { return compile_output; }
    public void setCompile_output(String compile_output) { this.compile_output = compile_output; }

    // Getter and Setter for statusId
    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }


    public Double getTime() { return time; }
    public void setTime(Double time) { this.time = time; }

    public Integer getMemory() { return memory; }
    public void setMemory(Integer memory) { this.memory = memory; }
}