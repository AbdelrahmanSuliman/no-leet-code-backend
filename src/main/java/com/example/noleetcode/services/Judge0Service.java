package com.example.noleetcode.services;

import com.example.noleetcode.Responses.Judge0Response;
import com.example.noleetcode.enums.Language;
import com.example.noleetcode.enums.SubmissionStatus;
import com.example.noleetcode.models.Submission;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class Judge0Service {

    private static final Logger logger = LoggerFactory.getLogger(Judge0Service.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final String JUDGE0_ENDPOINT = "https://judge0-ce.p.rapidapi.com/submissions?base64_encoded=false&wait=true";

    @Value("${JUDGE0_API_KEY}")
    private String JUDGE0_API_KEY;

    public Judge0Response submitToJudge0AndGetResult(String sourceCode, int languageId, String stdin)
            throws IOException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Build the request payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("source_code", sourceCode);
        payload.put("language_id", languageId);
        payload.put("stdin", stdin);

        // Convert payload to JSON string
        String requestJson = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(JUDGE0_ENDPOINT))
                .header("Content-Type", "application/json")
                .header("x-rapidapi-key", JUDGE0_API_KEY)
                .header("x-rapidapi-host", "judge0-ce.p.rapidapi.com")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        // Send the request and capture the response
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        String responseBody = response.body();

        logger.info("Judge0 API Response Status Code: {}", statusCode); // Log status code
        logger.info("Judge0 API Raw Response Body: {}", responseBody);

        // Parse the JSON response into our Judge0Response object
        return objectMapper.readValue(response.body(), Judge0Response.class);
    }

    public int getJudge0LanguageId(Language language) {
        if (language == null) {
            logger.error("Cannot map null language to Judge0 ID.");
            throw new IllegalArgumentException("Language cannot be null.");
        }

        return switch (language) {
            case JAVA -> 91;        // Java (JDK 17.0.6)
            case PYTHON -> 109;     // Python (3.13.2)
            case C -> 50;           // C (GCC 9.2.0)
            case CPP -> 54;         // C++ (GCC 9.2.0)
            case JAVASCRIPT -> 102; // JavaScript (Node.js 22.08.0)
            case RUBY -> 72;        // Ruby (2.7.0)
            case PHP -> 98;         // PHP (8.3.11)
        };
    }
    public SubmissionStatus mapJudge0Status(int judge0StatusId) {
        return switch (judge0StatusId) {
            case 1 -> SubmissionStatus.PENDING;
            case 2 -> SubmissionStatus.RUNNING;
            case 3 -> SubmissionStatus.ACCEPTED;
            case 4 -> SubmissionStatus.WRONG_ANSWER;
            case 5 -> SubmissionStatus.TIME_LIMIT_EXCEEDED;
            case 6 -> SubmissionStatus.COMPILATION_ERROR;
            case 7,
                 8,
                 9,
                 10,
                 11,
                 12,
                 14 -> SubmissionStatus.RUNTIME_ERROR;
            case 13 -> SubmissionStatus.INTERNAL_ERROR;
            default -> SubmissionStatus.RUNTIME_ERROR;
        };
    }


}
