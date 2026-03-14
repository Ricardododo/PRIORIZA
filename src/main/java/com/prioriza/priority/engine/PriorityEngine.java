package com.prioriza.priority.engine;

import com.prioriza.model.Task;
import com.prioriza.priority.model.PriorityLevel;
import com.prioriza.priority.rules.*;

import java.util.List;

/**
 * Motor de cálculo de prioridades para tareas.
 * 
 * Utiliza un conjunto de reglas heurísticas para calcular el nivel de prioridad
 * de una tarea basándose en: fecha de vencimiento, importancia, estado y subtareas.
 * 
 * Niveles: CRITICO (>=80) > URGENTE (>=50) > ALTO (>=30) > MEDIO (>=10) > BAJO
 * 
 * @author PRIORIZA
 * @version 1.0
 */
public class PriorityEngine {

    private final List<PriorityRule> rules = List.of(
            new OverdueRule(),
            new DueDateRule(),
            new SubtaskCountRule(),
            new ImportantRule(),
            new CompletionStatusRule()
    );

    /**
     * Calcula el nivel de prioridad de una tarea.
     * 
     * @param task La tarea a evaluar
     * @return Nivel de prioridad calculado
     */
    public PriorityLevel calculate(Task task){
        int score = rules.stream().mapToInt(r -> r.evaluate(task)).sum();

        if (score >= 80) return PriorityLevel.CRITICO;
        if (score >= 50) return PriorityLevel.URGENTE;
        if (score >= 30) return PriorityLevel.ALTO;
        if (score >= 10) return PriorityLevel.MEDIO;
        return PriorityLevel.BAJO;
    }

    /**
     * Calcula la puntuación numérica de prioridad de una tarea.
     * 
     * @param task La tarea a evaluar
     * @return Puntuación numérica (suma de todas las reglas)
     */
    public int calculateScore(Task task){
        return rules.stream().mapToInt(r -> r.evaluate(task)).sum();
    }

}
