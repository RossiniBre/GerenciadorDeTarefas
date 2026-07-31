package infrastructure.http.actions;

import domain.assistant.AssistantResponse;
import domain.assistant.Message;
import domain.assistant.MessageAuthor;
import domain.assistant.TaskAssistant;
import domain.model.User;
import infrastructure.http.dto.AssistantRequest;
import infrastructure.http.dto.MessageDto;
import infrastructure.http.json.JsonMapper;

import java.util.List;

public class AssistantAction {

    private final TaskAssistant taskAssistant;
    private final JsonMapper jsonMapper;

    public AssistantAction(
            TaskAssistant taskAssistant,
            JsonMapper jsonMapper
    ) {
        this.taskAssistant = taskAssistant;
        this.jsonMapper = jsonMapper;
    }

    public String execute(
            String body,
            User user
    ) {

        AssistantRequest request =
                jsonMapper.fromJson(
                        body,
                        AssistantRequest.class
                );

        List<Message> history = request.conversationHistory()
                .stream()
                .map(this::toMessage)
                .toList();

        System.out.println("ANTES DA IA");

        AssistantResponse response =
                taskAssistant.process(
                        history,
                        user.getId()
                );

        System.out.println("DEPOIS DA IA");

        return jsonMapper.toJson(response);
    }

    private Message toMessage(MessageDto dto) {

        return new Message(
                MessageAuthor.valueOf(
                        dto.author().toUpperCase()
                ),
                dto.content()
        );
    }
}