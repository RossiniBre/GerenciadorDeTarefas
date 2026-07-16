package infrastructure;

import domain.Task;
import domain.TaskBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskRepositoryTest {

    @Test
    void findAllByOwner_returnsOnlyTasksBelongingToThatOwner() {
        // Arrange
        InMemoryTaskRepository repo = new InMemoryTaskRepository();

        String ownerA = "owner-A";
        String ownerB = "owner-B";

        Task taskA1 = new TaskBuilder(ownerA).title("Task A1").build();
        Task taskA2 = new TaskBuilder(ownerA).title("Task A2").build();
        Task taskB1 = new TaskBuilder(ownerB).title("Task B1").build();

        repo.save(taskA1);
        repo.save(taskA2);
        repo.save(taskB1);

        // Act
        List<Task> tasksDoOwnerA = repo.findAllByOwner(ownerA);

        // Assert
        assertEquals(2, tasksDoOwnerA.size());
        assertTrue(tasksDoOwnerA.stream().allMatch(t -> t.getOwnerId().equals(ownerA)));
    }

    @Test
    void findAllByOwner_returnsEmptyListWhenOwnerHasNoTasks() {
        InMemoryTaskRepository repo = new InMemoryTaskRepository();

        List<Task> result = repo.findAllByOwner("owner-sem-tarefas");

        assertTrue(result.isEmpty());
    }
}