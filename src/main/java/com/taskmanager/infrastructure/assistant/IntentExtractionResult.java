package com.taskmanager.infrastructure.assistant;

import com.taskmanager.domain.assistant.TaskFilterIntent;
import java.util.List;

public record IntentExtractionResult(
        String type,
        List<SuggestionData> suggestions,
        TaskFilterIntent filter,
        String answer,
        String question,
        String reason
) {}