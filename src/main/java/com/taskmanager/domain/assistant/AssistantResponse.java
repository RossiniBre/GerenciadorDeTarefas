package com.taskmanager.domain.assistant;

import java.util.List;

public sealed interface AssistantResponse {

    record ValidSuggestions(List<TaskSuggestion> suggestions)
            implements AssistantResponse {}

    record OutOfScope(String reason)
            implements AssistantResponse {}

    record MissingInfos(String question)
            implements AssistantResponse {}

    record InformationalAnswer(String answer)
            implements AssistantResponse {}
}