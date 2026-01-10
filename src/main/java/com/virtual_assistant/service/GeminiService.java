package com.virtual_assistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    // 1. Simple Local Cache for repetitive "Identity" questions to save quota
    private final Map<String, String> identityCache = Map.of(
        "who created you", "{\"type\":\"creator_info\", \"userInput\":\"\", \"response\":\"Dipesh Sharma\"}",
        "who are you", "{\"type\":\"identity\", \"userInput\":\"\", \"response\":\"I am your virtual assistant created by Dipesh Sharma.\"}"
    );

    public String getGeminiResponse(String command, String assistantName, String userName) {
        String lowerCommand = command.toLowerCase().trim();

        // Check local cache first to save API quota
        for (String key : identityCache.keySet()) {
            if (lowerCommand.contains(key)) return identityCache.get(key);
        }

        return callGeminiWithRetry(command, assistantName, userName, 0);
    }

    private String callGeminiWithRetry(String command, String assistantName, String userName, int retryCount) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String prompt = """
                You are a virtual assistant named %s created by Dipesh Sharma and now you are talking with %s
                ... (keep your existing prompt instructions here) ...
                Now your user input: %s
                """.formatted(assistantName, userName, command);

            Map<String, Object> requestBody = Map.of("contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-goog-api-key", apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(GEMINI_URL, HttpMethod.POST, entity, Map.class);

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty()) return "{}";

            Map<String, Object> contentMap = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");

            return (String) parts.get(0).get("text");

        } catch (HttpClientErrorException.TooManyRequests e) {
            // 2. Handle 429 Specifically
            if (retryCount < 1) { // Retry once after a delay
                try {
                    System.out.println("Quota exceeded. Waiting 5 seconds before retry...");
                    Thread.sleep(5000); 
                    return callGeminiWithRetry(command, assistantName, userName, retryCount + 1);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            return "{\"type\":\"general\", \"response\":\"I'm a bit overwhelmed right now. Please wait a moment.\"}";
        
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"type\":\"general\", \"response\":\"Error: " + e.getMessage() + "\"}";
        }
    }
}
