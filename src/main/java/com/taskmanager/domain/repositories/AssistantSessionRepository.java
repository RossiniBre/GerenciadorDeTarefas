package com.taskmanager.domain.repositories;

import com.taskmanager.domain.assistant.AssistantSession;

import java.util.Optional;

public interface AssistantSessionRepository {
    Optional<AssistantSession> find(String token);
    void save(String token, AssistantSession session);
    void delete(String token);
}