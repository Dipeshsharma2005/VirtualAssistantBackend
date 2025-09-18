package com.virtual_assistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public String getGeminiResponse(String command, String assistantName, String userName) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Build the dynamic prompt
            String prompt = """
    You are a virtual assistant named %s created by Dipesh Sharma and now you are talking with %s

    Instructions:
    - "type": determine the intent of the user.
    - "userInput": the cleaned query (remove assistant’s name, remove filler words like "open", "search", "play", etc. Keep only the essential keywords the user wants).
    - "response": a short voice-friendly reply, e.g., "Sure, playing it now", "Here's what I found", "Today is Tuesday", etc.

    Type meanings:
    - "general": if it's a factual or informational question.aur agar koi aisa question puchta hai jiska answer tumhe pata hai usko bhi general ki category me rakho bas short answer dena jo main point ho   Examples: "tell me about Java", "send me info on AI", "who is Virat Kohli". \s
                                                                                                                                                                                                                              👉 Even if user says "send me about", "give me info", "tell me about", keep it in this category. \s
                                                                                                                                                                                                                              Always reply with a short, clear answer that gives only the main point. \s
    - "google_search": if user wants to search something on Google.
    - "youtube_search": if user wants to search something on YouTube.
    - "youtube_play": if user wants to directly play a video or song.
    - "calculator_open": if user wants to open a calculator.
    - "instagram_open": if user wants to open instagram.
    - "facebook_open": if user wants to open facebook.
    - "weather_show": if user wants to know weather.
    - "get_time": if user asks for current time.
    - "get_date": if user asks for today's date.
    - "get_day": if user asks what day it is.
    - "get_month": if user asks for the current month.
    - "creator_info": if user asks "who created you", "who is your creator", "who made you".
    - "identity": if user asks "tum kaun ho", "who are you", "what are you".

    Important:
    - If someone asks "who created you", reply with "Dipesh Sharma".
    - If someone asks "who are you", reply with "I am your virtual assistant created by Dipesh Sharma."
    - Only respond with the JSON object, nothing else.
    - The response MUST be a valid JSON object ONLY, starting with { and ending with }.
    - Do not include ```json or any extra explanation.
    - Tum ek virtual assistant ho.

    Now your user input: %s
    """.formatted(assistantName, userName, command);

            // Request body
            Map<String, Object> part = Map.of("text", prompt);
            Map<String, Object> content = Map.of("parts", List.of(part));
            Map<String, Object> requestBody = Map.of("contents", List.of(content));

            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-goog-api-key", apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Call Gemini API
            ResponseEntity<Map> response =
                    restTemplate.exchange(GEMINI_URL, HttpMethod.POST, entity, Map.class);

            // Navigate into candidates → content → parts → text
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "{}"; // return empty JSON if nothing comes back
            }

            Map<String, Object> contentMap = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");

            return (String) parts.get(0).get("text"); // raw JSON string inside text

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"type\":\"general\", \"userInput\":\"" + command + "\", \"response\":\"Error while calling Gemini API: " + e.getMessage() + "\"}";
        }
    }
}
