package com.prioriza.priority.rules;

import com.prioriza.model.Task;
import com.prioriza.model.TaskStatus;

public class CompletionStatusRule implements PriorityRule{

    @Override
    public int evaluate(Task task) {
        if (task.getStatus() == null) return 0;

        // Las tareas en progreso tienen más prioridad
        if (task.getStatus() == TaskStatus.EN_PROGRESO) {
            return 15;
        }

        // Las tareas completadas tienen prioridad 0
        if (task.getStatus() == TaskStatus.COMPLETA ||
                task.getStatus() == TaskStatus.CANCELADA) {
            return -999; // Prioridad negativa para que aparezcan al final
        }

        return 0;
    }
}
