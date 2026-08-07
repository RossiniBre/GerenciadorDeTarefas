package com.taskmanager.infrastructure.http.controllers;

import com.taskmanager.application.ConfirmTaskSuggestionUseCase;
import com.taskmanager.application.RejectTaskSuggestionUseCase;
import com.taskmanager.application.SendMessageToAssistantUseCase;
import com.taskmanager.domain.assistant.AssistantResponse;
import com.taskmanager.domain.model.User;
import com.taskmanager.infrastructure.http.AuthenticatedUser;
import com.taskmanager.infrastructure.http.dto.AssistantRequest;
import com.taskmanager.infrastructure.http.dto.SuggestionIdRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/assistant")
public class AssistantController {

    private final SendMessageToAssistantUseCase sendMessageToAssistantUseCase;
    private final ConfirmTaskSuggestionUseCase confirmTaskSuggestionUseCase;
    private final RejectTaskSuggestionUseCase rejectTaskSuggestionUseCase;

    public AssistantController(SendMessageToAssistantUseCase sendMessageToAssistantUseCase,
                               ConfirmTaskSuggestionUseCase confirmTaskSuggestionUseCase,
                               RejectTaskSuggestionUseCase rejectTaskSuggestionUseCase) {
        this.sendMessageToAssistantUseCase = sendMessageToAssistantUseCase;
        this.confirmTaskSuggestionUseCase = confirmTaskSuggestionUseCase;
        this.rejectTaskSuggestionUseCase = rejectTaskSuggestionUseCase;
    }

    @PostMapping("/message")
    public ResponseEntity<AssistantResponse> sendMessage(
            @AuthenticatedUser User user,
            @RequestBody AssistantRequest request) {

        AssistantResponse response = sendMessageToAssistantUseCase.execute(user.getId(), request.userMessage());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Map<String, String>> confirm(
            @AuthenticatedUser User user,
            @RequestBody SuggestionIdRequest request) {

        UUID suggestionId = UUID.fromString(request.suggestionId());
        confirmTaskSuggestionUseCase.execute(user.getId(), user, suggestionId);
        return ResponseEntity.ok(Map.of("status", "confirmed"));
    }

    @PostMapping("/reject")
    public ResponseEntity<Map<String, String>> reject(
            @AuthenticatedUser User user,
            @RequestBody SuggestionIdRequest request) {

        UUID suggestionId = UUID.fromString(request.suggestionId());
        rejectTaskSuggestionUseCase.execute(user.getId(), suggestionId);
        return ResponseEntity.ok(Map.of("status", "rejected"));
    }
}