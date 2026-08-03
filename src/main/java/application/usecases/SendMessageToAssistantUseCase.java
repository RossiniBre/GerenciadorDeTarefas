package application.usecases;

import application.usecases.ListTasksUseCase.TaskFilter;
import domain.assistant.*;
import domain.model.Task;
import domain.repositories.AssistantSessionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SendMessageToAssistantUseCase {

    private final AssistantSessionRepository assistantSessionRepository;
    private final TaskAssistant taskAssistant;
    private final ListTasksUseCase listTasksUseCase;

    public SendMessageToAssistantUseCase(
            AssistantSessionRepository assistantSessionRepository,
            TaskAssistant taskAssistant,
            ListTasksUseCase listTasksUseCase
    ) {
        this.assistantSessionRepository = assistantSessionRepository;
        this.taskAssistant = taskAssistant;
        this.listTasksUseCase = listTasksUseCase;
    }

    public AssistantResponse execute(String token, String userMessageText) {

        AssistantSession session =
                assistantSessionRepository
                        .find(token)
                        .orElse(new AssistantSession(
                                new ArrayList<>(),
                                new ArrayList<>()
                        ));

        List<Message> history =
                new ArrayList<>(session.conversationHistory());

        history.add(
                new Message(
                        MessageAuthor.USER,
                        userMessageText
                )
        );

        List<TaskSuggestion> pendingSuggestions =
                new ArrayList<>(session.pendingSuggestions());


        AssistantResponse response =
                taskAssistant.process(
                        new AssistantContext(
                                history,
                                pendingSuggestions,
                                token
                        )
                );


        String assistantMessageText;
        List<UUID> suggestionIds = List.of();


        switch (response) {

            case AssistantResponse.ValidSuggestions valid -> {

                pendingSuggestions.addAll(valid.suggestions());

                assistantMessageText =
                        summarize(
                                valid.suggestions(),
                                token
                        );

                suggestionIds =
                        valid.suggestions()
                                .stream()
                                .map(TaskSuggestion::id)
                                .toList();
            }

            case AssistantResponse.MissingInfos missing ->
                    assistantMessageText = missing.question();

            case AssistantResponse.OutOfScope outOfScope ->
                    assistantMessageText = outOfScope.reason();

            case AssistantResponse.InformationalAnswer informational ->
                    assistantMessageText = informational.answer();
        }


        history.add(
                new Message(
                        MessageAuthor.ASSISTANT,
                        assistantMessageText,
                        suggestionIds
                )
        );


        assistantSessionRepository.save(
                token,
                new AssistantSession(
                        history,
                        pendingSuggestions
                )
        );


        return response;
    }


    private String summarize(
            List<TaskSuggestion> suggestions,
            String ownerId
    ) {

        List<Task> tasks =
                listTasksUseCase.execute(
                        ownerId,
                        TaskFilter.none()
                );


        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < suggestions.size(); i++) {

            sb.append(
                    describe(
                            suggestions.get(i),
                            tasks
                    )
            );

            if (i < suggestions.size() - 1) {
                sb.append("; ");
            }
        }

        return sb.toString();
    }


    private String describe(
            TaskSuggestion suggestion,
            List<Task> tasks
    ) {

        return switch (suggestion) {

            case TaskSuggestion.Create s ->
                    "Sugeri criar a tarefa: \""
                            + s.title()
                            + "\". Aguardando confirmação.";


            case TaskSuggestion.Update s ->
                    "Sugeri atualizar a tarefa \""
                            + findTitle(s.targetTaskId(), tasks)
                            + "\". Aguardando confirmação.";


            case TaskSuggestion.Delete s ->
                    "Sugeri apagar a tarefa \""
                            + findTitle(s.targetTaskId(), tasks)
                            + "\". Aguardando confirmação.";


            case TaskSuggestion.Start s ->
                    "Sugeri iniciar a tarefa \""
                            + findTitle(s.targetTaskId(), tasks)
                            + "\". Aguardando confirmação.";


            case TaskSuggestion.Complete s ->
                    "Sugeri concluir a tarefa \""
                            + findTitle(s.targetTaskId(), tasks)
                            + "\". Aguardando confirmação.";
        };
    }


    private String findTitle(
            String taskId,
            List<Task> tasks
    ) {
        return tasks.stream()
                .filter(task -> task.getId().equals(taskId))
                .map(Task::getTitle)
                .findFirst()
                .orElse("tarefa desconhecida");
    }
}