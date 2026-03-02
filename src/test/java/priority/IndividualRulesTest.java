package priority;

import com.prioriza.model.Task;
import com.prioriza.model.TaskStatus;
import com.prioriza.priority.rules.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class IndividualRulesTest {

    private LocalDateTime now = LocalDateTime.now();

    @Test
    @DisplayName("DueDateRule - prueba de todos los rangos")
    void testDueDateRule() {
        DueDateRule rule = new DueDateRule();
        Task task = new Task();

        // Vence en 2 horas
        task.setDueDateTime(now.plusHours(2));
        assertEquals(40, rule.evaluate(task));

        // Vence en 6 horas
        task.setDueDateTime(now.plusHours(6));
        assertEquals(30, rule.evaluate(task));

        // Vence mañana
        task.setDueDateTime(now.plusDays(1));
        assertEquals(20, rule.evaluate(task));

        // Vence en 3 días
        task.setDueDateTime(now.plusDays(3));
        assertEquals(10, rule.evaluate(task));

        // Vence en 10 días
        task.setDueDateTime(now.plusDays(10));
        assertEquals(0, rule.evaluate(task));

        // Sin fecha
        task.setDueDateTime(null);
        assertEquals(0, rule.evaluate(task));
    }

    @Test
    @DisplayName("OverdueRule - tareas vencidas")
    void testOverdueRule() {
        OverdueRule rule = new OverdueRule();
        Task task = new Task();

        // Vencida ayer
        task.setDueDateTime(now.minusDays(1));
        assertEquals(50, rule.evaluate(task));

        // Vencida hace una hora
        task.setDueDateTime(now.minusHours(1));
        assertEquals(50, rule.evaluate(task));

        // No vencida
        task.setDueDateTime(now.plusDays(1));
        assertEquals(0, rule.evaluate(task));
    }

    @Test
    @DisplayName("ImportantRule - tareas importantes")
    void testImportantRule() {
        ImportantRule rule = new ImportantRule();
        Task task = new Task();

        task.setImportant(true);
        assertEquals(20, rule.evaluate(task));

        task.setImportant(false);
        assertEquals(0, rule.evaluate(task));
    }

    @Test
    @DisplayName("CompletionStatusRule - estado de la tarea")
    void testCompletionStatusRule() {
        CompletionStatusRule rule = new CompletionStatusRule();
        Task task = new Task();

        task.setStatus(TaskStatus.EN_PROGRESO);
        assertEquals(15, rule.evaluate(task));

        task.setStatus(TaskStatus.PENDIENTE);
        assertEquals(0, rule.evaluate(task));

        task.setStatus(TaskStatus.COMPLETA);
        assertEquals(-999, rule.evaluate(task));

        task.setStatus(TaskStatus.CANCELADA);
        assertEquals(-999, rule.evaluate(task));
    }
}
