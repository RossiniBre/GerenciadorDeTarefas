package domain;

import application.usecases.ListTasksUseCase.TaskFilter;
import domain.assistant.TaskFilterIntent;
import domain.assistant.TaskFilterResolver;
import domain.model.TaskCategory;
import domain.model.TaskPriority;
import domain.model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskFilterResolverTest {

    private final TaskFilterResolver resolver = new TaskFilterResolver();

    @Test
    void intentNulo_devolveFilterNone() {
        TaskFilter result = resolver.resolve(null);

        assertEquals(TaskFilter.none(), result);
    }

    @Test
    void todosCamposNulos_devolveFilterNone() {
        TaskFilterIntent intent = new TaskFilterIntent(null, null, null, null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(TaskFilter.none(), result);
    }

    @Test
    void valoresValidos_saoResolvidosCorretamente() {
        TaskFilterIntent intent = new TaskFilterIntent("PENDING", "HIGH", null, null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(TaskStatus.PENDING, result.status());
        assertEquals(TaskPriority.HIGH, result.priority());
        assertEquals(null, result.category());
        assertEquals(Set.of(), result.excludedStatuses());
    }

    @Test
    void valorEmMinusculoOuComEspacos_eNormalizado() {
        TaskFilterIntent intent = new TaskFilterIntent("  pending ", "high", null, null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(TaskStatus.PENDING, result.status());
        assertEquals(TaskPriority.HIGH, result.priority());
    }

    @Test
    void valorAlucinado_naoQuebraEViraNull() {
        TaskFilterIntent intent = new TaskFilterIntent("URGENTE_TOTAL", null, null, null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(null, result.status());
        assertEquals(TaskFilter.none(), result);
    }

    @Test
    void misturaDeValorValidoEAlucinado_ignoraSoOInvalido() {
        TaskFilterIntent intent = new TaskFilterIntent("PENDING", "PRIORIDADE_INEXISTENTE", null, null);

        TaskFilter result = resolver.resolve(intent);

        assertEquals(TaskStatus.PENDING, result.status());
        assertEquals(null, result.priority());
    }

    @Test
    void excludeStatus_geraExcludedStatusesComUmElemento() {
        TaskFilterIntent intent = new TaskFilterIntent(null, null, null, "COMPLETED");

        TaskFilter result = resolver.resolve(intent);

        assertEquals(EnumSet.of(TaskStatus.COMPLETED), result.excludedStatuses());
        assertEquals(null, result.status());
    }

    @Test
    void excludeStatusAlucinado_devolveExcludedStatusesVazio() {
        TaskFilterIntent intent = new TaskFilterIntent(null, null, null, "NAO_EXISTE");

        TaskFilter result = resolver.resolve(intent);

        assertEquals(Set.of(), result.excludedStatuses());
    }

    @Test
    void statusEExcludeStatusJuntos_naoSeConflitam_ficamComoVieram() {
        TaskFilterIntent intent = new TaskFilterIntent("IN_PROGRESS", null, null, "COMPLETED");

        TaskFilter result = resolver.resolve(intent);

        assertEquals(TaskStatus.IN_PROGRESS, result.status());
        assertEquals(EnumSet.of(TaskStatus.COMPLETED), result.excludedStatuses());
    }
}