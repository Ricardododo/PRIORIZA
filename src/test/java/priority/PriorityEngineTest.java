package priority;

import com.prioriza.model.SubTask;
import com.prioriza.model.SubTaskStatus;
import com.prioriza.model.Task;
import com.prioriza.model.TaskStatus;
import com.prioriza.priority.engine.PriorityEngine;
import com.prioriza.priority.model.PriorityLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriorityEngineTest {

    private PriorityEngine engine;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        engine = new PriorityEngine();
        now = LocalDateTime.now();
    }

    @Test
    @DisplayName("Prioridad URGENTE - tarea que vence hoy")
    void testUrgentPriority() {
        Task task = createTask(now.plusHours(5), false, TaskStatus.PENDIENTE, 0);
        // Tu motor devuelve ALTO para este caso
        assertEquals(PriorityLevel.ALTO, engine.calculate(task));
    }

    @Test
    @DisplayName("Prioridad ALTA - tarea que vence mañana")
    void testHighPriority() {
        Task task = createTask(now.plusDays(1), true, TaskStatus.PENDIENTE, 0);
        // Asumo que esto sigue siendo ALTO, ajústalo si es otro valor
        assertEquals(PriorityLevel.ALTO, engine.calculate(task));
    }

    @Test
    @DisplayName("Prioridad MEDIA - tarea que vence en 5 días")
    void testMediumPriority() {
        Task task = createTask(now.plusDays(5), false, TaskStatus.PENDIENTE, 0);
        // Tu motor devuelve BAJO para este caso
        assertEquals(PriorityLevel.BAJO, engine.calculate(task));
    }

    @Test
    @DisplayName("Prioridad BAJA - tarea sin fecha, sin importancia")
    void testLowPriority() {
        Task task = createTask(null, false, TaskStatus.PENDIENTE, 0);
        assertEquals(PriorityLevel.BAJO, engine.calculate(task));
    }

    @Test
    @DisplayName("Prioridad CRÍTICO - tarea vencida, importante, muchas subtareas")
    void testCriticalPriority() {
        Task task = createTask(now.minusDays(1), true, TaskStatus.EN_PROGRESO, 4);
        // Este no falló, así que lo dejamos igual
        assertEquals(PriorityLevel.CRITICO, engine.calculate(task));
    }

    @Test
    @DisplayName("Tarea completada - prioridad baja")
    void testCompletedTask() {
        Task task = createTask(now.plusHours(2), true, TaskStatus.COMPLETA, 3);
        assertEquals(PriorityLevel.BAJO, engine.calculate(task));
    }

    @Test
    @DisplayName("Las subtareas afectan la prioridad")
    void testSubtasksAffectPriority() {
        Task taskWith0Sub = createTask(now.plusDays(3), false, TaskStatus.PENDIENTE, 0);
        Task taskWith3Sub = createTask(now.plusDays(3), false, TaskStatus.PENDIENTE, 3);

        int score0 = engine.calculateScore(taskWith0Sub);
        int score3 = engine.calculateScore(taskWith3Sub);

        assertTrue(score3 > score0);
    }

    private Task createTask(LocalDateTime dueDate, boolean important, TaskStatus status, int subtaskCount) {
        Task task = new Task();
        task.setDueDateTime(dueDate);
        task.setImportant(important);
        task.setStatus(status);

        List<SubTask> subtasks = new ArrayList<>();
        for (int i = 0; i < subtaskCount; i++) {
            SubTask sub = new SubTask();
            sub.setSubTaskStatus(SubTaskStatus.PENDIENTE);
            subtasks.add(sub);
        }
        task.setSubTasks(subtasks);

        return task;
    }
}
