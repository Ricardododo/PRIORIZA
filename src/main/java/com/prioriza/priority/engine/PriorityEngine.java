package com.prioriza.priority.engine;

import com.prioriza.model.Task;
import com.prioriza.priority.model.PriorityLevel;
import com.prioriza.priority.rules.*;

import java.util.List;

public class PriorityEngine {

    private final List<PriorityRule> rules = List.of(
            new OverdueRule(),
            new DueDateRule(),
            new SubtaskCountRule(),
            new ImportantRule(),
            new CompletionStatusRule()
    );
    //este metodo devuelve el nivel de prioridad
    public PriorityLevel calculate(Task task){
        int score = rules.stream().mapToInt(r -> r.evaluate(task)).sum();

        if (score >= 80) return PriorityLevel.CRITICO;
        if (score >= 50) return PriorityLevel.URGENTE;
        if (score >= 30) return PriorityLevel.ALTO;
        if (score >= 10) return PriorityLevel.MEDIO;
        return PriorityLevel.BAJO;
    }
    //este metodo devuelve la puntuacion numerica
    public int calculateScore(Task task){
        return rules.stream().mapToInt(r -> r.evaluate(task)).sum();
    }

}
