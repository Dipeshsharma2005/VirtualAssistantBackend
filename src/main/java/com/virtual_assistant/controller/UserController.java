package com.virtual_assistant.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.virtual_assistant.dto.AssistantResponse;
import com.virtual_assistant.dto.CommandRequest;
import com.virtual_assistant.dto.UserDTO;
import com.virtual_assistant.dto.UserPrincipal;
import com.virtual_assistant.model.User;
import com.virtual_assistant.repository.UserRepository;
import com.virtual_assistant.service.AuthService;
import com.virtual_assistant.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final GeminiService geminiService;

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // ✅ Fetch full user entity (with history)
        Optional<User> optionalUser = userRepository.findById(principal.getId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = optionalUser.get();

        // ✅ Map to DTO (including history)
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAssistantName(),
                user.getAssistantImage(),
                user.getHistory()  // <-- this will now be included
        );

        return ResponseEntity.ok(userDTO);
    }



    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAssistant(
            @PathVariable Long id,
            @RequestParam(required = false) String assistantName,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) MultipartFile assistantImage
    ) {
        try {
            Optional<UserDTO> updatedUser = authService.updateAssistant(id, assistantName, imageUrl, assistantImage);
            return updatedUser.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating assistant: " + e.getMessage());
        }
    }

    @PostMapping("/ask")
    public ResponseEntity<?> askToAssistant(@RequestBody CommandRequest request,
                                            @RequestParam("userId") Long userId) {
        try {
            // Fetch user
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }
            User user = optionalUser.get();

            // Call Gemini API → now returns raw JSON string
            String result = geminiService.getGeminiResponse(
                    request.getCommand(),
                    user.getAssistantName(),
                    user.getName()
            );

            // Clean Gemini output → remove markdown or backticks
            String cleanResult = result.trim()
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

// Parse Gemini JSON safely
            ObjectMapper mapper = new ObjectMapper();
            JsonNode gemResult = mapper.readTree(cleanResult);


            String type = gemResult.has("type") ? gemResult.get("type").asText() : "general";
            String userInput = gemResult.has("userInput") ? gemResult.get("userInput").asText() : request.getCommand();
            String response = gemResult.has("response") ? gemResult.get("response").asText() : "I'm not sure how to answer that.";

            if (user.getHistory() == null) {
                user.setHistory(new ArrayList<>());
            }
            user.getHistory().add("User: " + userInput + " | Assistant: " + response);
            userRepository.save(user);
// Handle types (snake_case everywhere)
            switch (type) {
                case "get_date":
                    return ResponseEntity.ok(
                            new AssistantResponse(type, userInput,
                                    "Today is " + LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy")))
                    );

                case "get_time":
                    return ResponseEntity.ok(
                            new AssistantResponse(type, userInput,
                                    "It’s " + LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a")))
                    );

                case "get_day":
                    String day = capitalize(LocalDate.now().getDayOfWeek().toString());
                    return ResponseEntity.ok(
                            new AssistantResponse(type, userInput, "Today is " + day)
                    );

                case "get_month":
                    String month = capitalize(LocalDate.now().getMonth().toString());
                    return ResponseEntity.ok(
                            new AssistantResponse(type, userInput, "The current month is " + month)
                    );


                case "creator_info":
                    return ResponseEntity.ok(
                            new AssistantResponse(type, userInput, "Dipesh Sharma")
                    );

                case "identity":
                    return ResponseEntity.ok(
                            new AssistantResponse(type, userInput, "I am your virtual assistant created by Dipesh Sharma.")
                    );

                case "google_search":
                case "youtube_search":
                case "youtube_play":
                case "general":
                case "calculator_open":
                case "instagram_open":
                case "facebook_open":
                case "weather_show":
                    return ResponseEntity.ok(new AssistantResponse(type, userInput, response));

                default:
                    return ResponseEntity.badRequest().body(
                            new AssistantResponse("general", userInput, "I didn't understand that command.")
                    );
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new AssistantResponse("general", request.getCommand(), "Error: " + e.getMessage())
            );
        }
    }
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }


}

