package domain;

import domain.assistant.*;
import domain.model.TaskCategory;
import domain.model.TaskPriority;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssistantResponseTest {

    @Test
    void deveCriarValidSuggestionsComListaDeSugestoes() {
        TaskSuggestion suggestion = new TaskSuggestion(
                "Estudar Java",
                "Revisar sealed interfaces",
                TaskCategory.STUDY,
                TaskPriority.MEDIUM
        );

        AssistantResponse response = new AssistantResponse.ValidSuggestions(List.of(suggestion));

        assertTrue(response instanceof AssistantResponse.ValidSuggestions);
    }

    @Test
    void deveCriarOutOfScopeComMotivo() {
        AssistantResponse response = new AssistantResponse.OutOfScope("Isso não é sobre criar tarefas.");

        assertTrue(response instanceof AssistantResponse.OutOfScope);
    }

    @Test
    void deveCriarMissingInfosComPergunta() {
        AssistantResponse response = new AssistantResponse.MissingInfos("Qual o título da tarefa?");

        assertTrue(response instanceof AssistantResponse.MissingInfos);
    }

    @Test
    void switchExaustivoDeveTratarAsTresVariantes() {
        AssistantResponse response = new AssistantResponse.OutOfScope("Fora de escopo.");

        String resultado = switch (response) {
            case AssistantResponse.ValidSuggestions vs -> "sugestões: " + vs.suggestions().size();
            case AssistantResponse.OutOfScope oos -> "recusado: " + oos.reason();
            case AssistantResponse.MissingInfos mi -> "pergunta: " + mi.question();
        };

        assertEquals("recusado: Fora de escopo.", resultado);
    }

    @Test
    void assistantDeveSerImplementavelComoStrategy() {
        TaskCreationAssistant fakeAssistant = messages ->
                new AssistantResponse.MissingInfos("Qual o título?");

        Message userMessage = new Message(MessageAuthor.USER, "cria uma tarefa");

        AssistantResponse response = fakeAssistant.process(List.of(userMessage));

        assertInstanceOf(AssistantResponse.MissingInfos.class, response);
    }
}