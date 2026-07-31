package domain.assistant;

import application.usecases.ListTasksUseCase;
import domain.model.Task;
import domain.model.TaskCategory;
import domain.model.TaskPriority;
import infrastructure.assistant.IntentExtractionResult;
import infrastructure.assistant.SuggestionData;
import infrastructure.http.json.JsonMapper;

import java.util.List;
import java.util.UUID;

public class TaskAssistantOrchestrator implements TaskAssistant {

    private final IntentExtractor intentExtractor;
    private final AnswerFormatter answerFormatter;
    private final TaskFilterResolver taskFilterResolver;
    private final ListTasksUseCase listTasksUseCase;
    private final JsonMapper jsonMapper;
    private final String systemInstructions;

    public TaskAssistantOrchestrator(
            IntentExtractor intentExtractor,
            AnswerFormatter answerFormatter,
            TaskFilterResolver taskFilterResolver,
            ListTasksUseCase listTasksUseCase,
            JsonMapper jsonMapper,
            String systemInstructions
    ) {
        if (intentExtractor == null) {
            throw new IllegalArgumentException("IntentExtractor é obrigatório!");
        }
        if (answerFormatter == null) {
            throw new IllegalArgumentException("AnswerFormatter é obrigatório!");
        }
        if (taskFilterResolver == null) {
            throw new IllegalArgumentException("TaskFilterResolver é obrigatório!");
        }
        if (listTasksUseCase == null) {
            throw new IllegalArgumentException("ListTasksUseCase é obrigatório!");
        }
        if (jsonMapper == null) {
            throw new IllegalArgumentException("JsonMapper é obrigatório!");
        }
        if (systemInstructions == null || systemInstructions.isBlank()) {
            throw new IllegalArgumentException("Instruções do sistema são obrigatórias!");
        }

        this.intentExtractor = intentExtractor;
        this.answerFormatter = answerFormatter;
        this.taskFilterResolver = taskFilterResolver;
        this.listTasksUseCase = listTasksUseCase;
        this.jsonMapper = jsonMapper;
        this.systemInstructions = systemInstructions;
    }

    @Override
    public AssistantResponse process(List<Message> conversationHistory, String requesterId) {
        String userMessage = lastUserMessage(conversationHistory);

        String rawJson = intentExtractor.extract(systemInstructions, userMessage);
        IntentExtractionResult result = jsonMapper.fromJson(rawJson, IntentExtractionResult.class);

        return switch (result.type()) {
            case "SUGGESTIONS" -> new AssistantResponse.ValidSuggestions(toSuggestions(result.suggestions()));
            case "LISTING" -> handleListing(result.filter(), requesterId);
            case "MISSING_INFO" -> new AssistantResponse.MissingInfos(result.question());
            case "OUT_OF_SCOPE" -> new AssistantResponse.OutOfScope(result.reason());
            default -> new AssistantResponse.InformationalAnswer(result.answer());
        };
    }

    private AssistantResponse handleListing(TaskFilterIntent filterIntent, String requesterId) {
        ListTasksUseCase.TaskFilter filter = taskFilterResolver.resolve(filterIntent);
        List<Task> tasks = listTasksUseCase.execute(requesterId, filter);
        String formatted = answerFormatter.format(
                "Liste as tarefas de forma clara e organizada", jsonMapper.toJson(tasks));
        return new AssistantResponse.InformationalAnswer(formatted);
    }

    private List<TaskSuggestion> toSuggestions(List<SuggestionData> raw) {
        return raw.stream().map(this::toSuggestion).toList();
    }

    private TaskSuggestion toSuggestion(SuggestionData data) {
        UUID id = UUID.randomUUID();
        return switch (data.action()) {
            case "CREATE" -> new TaskSuggestion.Create(id, data.title(), data.description(),
                    EnumParser.parse(TaskPriority.class, data.priority()),
                    EnumParser.parse(TaskCategory.class, data.category()));
            case "UPDATE" -> new TaskSuggestion.Update(id, data.targetTaskId(), data.title(), data.description(),
                    EnumParser.parse(TaskPriority.class, data.priority()),
                    EnumParser.parse(TaskCategory.class, data.category()));
            case "DELETE" -> new TaskSuggestion.Delete(id, data.targetTaskId());
            case "START" -> new TaskSuggestion.Start(id, data.targetTaskId());
            case "COMPLETE" -> new TaskSuggestion.Complete(id, data.targetTaskId());
            default -> throw new IllegalStateException("Ação desconhecida: " + data.action());
        };
    }

    private String lastUserMessage(List<Message> history) {
        for (int i = history.size() - 1; i >= 0; i--) {
            Message message = history.get(i);
            if (message.author() == MessageAuthor.USER) {
                return message.content();
            }
        }
        throw new IllegalArgumentException("Histórico não contém nenhuma mensagem do usuário!");
    }
}