package model;

import com.prioriza.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ModelTests {

    @Test
    @DisplayName("Test de creación de usuario")
    void testUserCreation() {
        User user = new User("Juan Pérez", "juan@email.com", "password123");
        user.setId(1);
        user.setRole(UserRole.USER);

        assertEquals(1, user.getId());
        assertEquals("Juan Pérez", user.getName());
        assertEquals("juan@email.com", user.getEmail());
        assertEquals(UserRole.USER, user.getRole());
    }

    @Test
    @DisplayName("Test de creación de tarea")
    void testTaskCreation() {
        Task task = new Task();
        task.setId(1);
        task.setTitle("Tarea de prueba");
        task.setDescription("Descripción de prueba");
        task.setDueDateTime(LocalDateTime.now().plusDays(1));
        task.setPriority(Priority.ALTA);
        task.setStatus(TaskStatus.PENDIENTE);
        task.setImportant(true);
        task.setTaskListId(10);

        assertEquals(1, task.getId());
        assertEquals("Tarea de prueba", task.getTitle());
        assertEquals("Descripción de prueba", task.getDescription());
        assertEquals(Priority.ALTA, task.getPriority());
        assertEquals(TaskStatus.PENDIENTE, task.getStatus());
        assertTrue(task.isImportant());
        assertEquals(10, task.getTaskListId());
    }

    @Test
    @DisplayName("Test de creación de subtarea")
    void testSubTaskCreation() {
        SubTask subTask = new SubTask();
        subTask.setId(5);
        subTask.setTitle("Subtarea de prueba");
        subTask.setTaskId(1);
        subTask.setSubTaskStatus(SubTaskStatus.PENDIENTE);
        subTask.setImportant(true);
        subTask.setDueDateTime(LocalDateTime.now().plusDays(2));

        assertEquals(5, subTask.getId());
        assertEquals("Subtarea de prueba", subTask.getTitle());
        assertEquals(1, subTask.getTaskId());
        assertEquals(SubTaskStatus.PENDIENTE, subTask.getSubTaskStatus());
        assertTrue(subTask.isImportant());
    }

    @Test
    @DisplayName("Test de marcar subtarea como completada")
    void testMarkSubTaskCompleted() {
        SubTask subTask = new SubTask();
        subTask.setSubTaskStatus(SubTaskStatus.PENDIENTE);

        subTask.markCompleted();

        assertEquals(SubTaskStatus.COMPLETA, subTask.getSubTaskStatus());
    }
}
