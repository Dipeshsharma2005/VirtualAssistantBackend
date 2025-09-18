package com.virtual_assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssistantResponse {
    private String type;
    private String userInput;
    private String response;

    public AssistantResponse(String response) {
        this.response = response;
    }
}
