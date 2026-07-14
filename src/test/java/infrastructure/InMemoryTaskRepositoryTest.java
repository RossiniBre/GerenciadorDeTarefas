package infrastructure;

import domain.Task;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskRepositoryTest {

    @Test
    void shouldHaveOneElementSize() {
        InMemoryTaskRepository repo = new InMemoryTaskRepository();
        Task task = Task.newTask("Estudar Java", "Praticar testes unitários");

        repo.save(task);

        assertEquals(1, repo.findAll().size());
    }

    @Test
    void shouldUpdateWithoutDuplicatingWhenSavingAgain() {
        // Arrange: prepara o cenário
        InMemoryTaskRepository repo = new InMemoryTaskRepository();
        Task task = Task.newTask("Titulo original", "Descricao original");
        repo.save(task);

        // Act: executa a ação que quero testar
        task.updateDetails("Titulo novo", "Descricao nova");
        repo.save(task);

        // Assert: confiro se o resultado é o esperado
        assertEquals(1, repo.findAll().size());
        assertEquals("Titulo novo", repo.findById(task.getId()).get().getTitle());
    }
}