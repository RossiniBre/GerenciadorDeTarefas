package application.usecases;

import domain.assistant.*;
import domain.repositories.AssistantSessionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SendMessageToAssistantUseCase {

    private final AssistantSessionRepository assistantSessionRepository;
    private final TaskAssistant taskAssistant;

    public SendMessageToAssistantUseCase(
            AssistantSessionRepository assistantSessionRepository,
            TaskAssistant taskAssistant
    ) {
        this.assistantSessionRepository = assistantSessionRepository;
        this.taskAssistant = taskAssistant;
    }

    public AssistantResponse execute(String token, String userMessageText) {
        AssistantSession session = assistantSessionRepository.find(token).orElse(new AssistantSession(new ArrayList<>(), new ArrayList<>()));

        List<Message> history = new ArrayList<>(session.conversationHistory());
        history.add(new Message(MessageAuthor.USER, userMessageText));

        AssistantResponse response = taskAssistant.process(history, token);

        List<TaskSuggestion> pendingSuggestions = new ArrayList<>(session.pendingSuggestions());
        String assistantMessageText;
        List<UUID> suggestionIds = List.of();

        switch (response) {
            case AssistantResponse.ValidSuggestions valid -> {
                pendingSuggestions.addAll(valid.suggestions());
                assistantMessageText = summarize(valid.suggestions());
                suggestionIds = valid.suggestions().stream().map(TaskSuggestion::id).toList();
            }
            case AssistantResponse.MissingInfos missing ->
                    assistantMessageText = missing.question();
            case AssistantResponse.OutOfScope outOfScope ->
                    assistantMessageText = outOfScope.reason();
            case AssistantResponse.InformationalAnswer informational ->
                    assistantMessageText = informational.answer();
        }

        history.add(new Message(MessageAuthor.ASSISTANT, assistantMessageText, suggestionIds));

        assistantSessionRepository.save(token, new AssistantSession(history, pendingSuggestions));

        return response;
    }

    private String summarize(List<TaskSuggestion> suggestions) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < suggestions.size(); i++) {
            sb.append(describe(suggestions.get(i)));
            if (i < suggestions.size() - 1) sb.append("; ");
        }
        return sb.toString();
    }

    private String describe(TaskSuggestion suggestion) {
        return switch (suggestion) {
            case TaskSuggestion.Create s ->
                    "Sugeri criar a tarefa: " + s.title();

            case TaskSuggestion.Update s ->
                    "Sugeri atualizar a tarefa " + s.targetTaskId();

            case TaskSuggestion.Delete s ->
                    "Sugeri apagar a tarefa " + s.targetTaskId();

            case TaskSuggestion.Start s ->
                    "Sugeri iniciar a tarefa " + s.targetTaskId();

            case TaskSuggestion.Complete s ->
                    "Sugeri concluir a tarefa " + s.targetTaskId();
        };
    }
}