package com.prioriza.priority.rules;

import com.prioriza.model.Task;

/**
 * Interfaz para las reglas de cálculo de prioridad.
 * 
 * Cada regla implementada debe evaluar una tarea y devolver una puntuación
 * que se suma al cálculo total de prioridad.
 * 
 * @author PRIORIZA
 * @version 1.0
 */
public interface PriorityRule {
    /**
     * Evalúa una tarea y devuelve una puntuación.
     * 
     * @param task La tarea a evaluar
     * @return Puntuación aportada por esta regla
     */
    int evaluate(Task task);
}
