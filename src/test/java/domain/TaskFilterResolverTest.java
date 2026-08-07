package domain;

import com.taskmanager.application.ListTasksUseCase.TaskFilter;
import com.taskmanager.domain.assistant.TaskFilterIntent;
import com.taskmanager.domain.assistant.TaskFilterResolver;
import com.taskmanager.domain.model.TaskPriority;
import com.taskmanager.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskFilterResolverTest {

    private final TaskFilterResolver resolver = new TaskFilterResolver();

    @Test
    void nullIntent_returnsFilterNone() {
        TaskFilter result = resolver.resolve(null);

        assertEquals(TaskFilter.none(), result);
    }

    @Test
    void allFieldsNull_returnsFilterNone() {
        TaskFilterIntent intent = new TaskFilterIntent(null, null, null, null, null, null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(TaskFilter.none(), result);
    }

    @Test
    void validValues_areResolvedCorrectly() {
        TaskFilterIntent intent = new TaskFilterIntent("PENDING", "HIGH", null, null, null, null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(TaskStatus.PENDING, result.status());
        assertEquals(TaskPriority.HIGH, result.priority());
        assertEquals(null, result.category());
        assertEquals(Set.of(), result.excludedStatuses());
    }

    @Test
    void lowercaseOrPaddedValue_isNormalized() {
        TaskFilterIntent intent = new TaskFilterIntent("  pending ", "high", null, null, null, null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(TaskStatus.PENDING, result.status());
        assertEquals(TaskPriority.HIGH, result.priority());
    }

    @Test
    void hallucinatedValue_doesNotBreakAndBecomesNull() {
        TaskFilterIntent intent = new TaskFilterIntent("URGENTE_TOTAL", null, null, null, null, null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(null, result.status());
        assertEquals(TaskFilter.none(), result);
    }

    @Test
    void mixOfValidAndHallucinatedValue_ignoresOnlyTheInvalidOne() {
        TaskFilterIntent intent = new TaskFilterIntent("PENDING", "PRIORIDADE_INEXISTENTE", null, null, null, null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(TaskStatus.PENDING, result.status());
        assertEquals(null, result.priority());
    }

    @Test
    void excludeStatus_generatesExcludedStatusesWithOneElement() {
        TaskFilterIntent intent = new TaskFilterIntent(null, null, null, "COMPLETED", null, null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(EnumSet.of(TaskStatus.COMPLETED), result.excludedStatuses());
        assertEquals(null, result.status());
    }

    @Test
    void hallucinatedExcludeStatus_returnsEmptyExcludedStatuses() {
        TaskFilterIntent intent = new TaskFilterIntent(null, null, null, "NAO_EXISTE", null, null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(Set.of(), result.excludedStatuses());
    }

    @Test
    void statusAndExcludeStatusTogether_doNotConflict_remainAsGiven() {
        TaskFilterIntent intent = new TaskFilterIntent("IN_PROGRESS", null, null, "COMPLETED", null, null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(TaskStatus.IN_PROGRESS, result.status());
        assertEquals(EnumSet.of(TaskStatus.COMPLETED), result.excludedStatuses());
    }

    @Test
    void validDueDateRange_isParsedCorrectly() {
        TaskFilterIntent intent = new TaskFilterIntent(null, null, null, null,
                "2026-08-05T00:00:00", "2026-08-06T00:00:00");

        TaskFilter result = resolver.resolve(intent);

        assertEquals(java.time.LocalDateTime.parse("2026-08-05T00:00:00"), result.dueDateFrom());
        assertEquals(java.time.LocalDateTime.parse("2026-08-06T00:00:00"), result.dueDateTo());
    }

    @Test
    void invalidDueDate_isIgnoredAndBecomesNull() {
        TaskFilterIntent intent = new TaskFilterIntent(null, null, null, null, "data-invalida", null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(null, result.dueDateFrom());
    }

    @Test
    void blankDueDate_isTreatedAsNull() {
        TaskFilterIntent intent = new TaskFilterIntent(null, null, null, null, "   ", null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(null, result.dueDateFrom());
    }
}