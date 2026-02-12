package com.prioriza.priority.rules;

import com.prioriza.model.Task;
import com.prioriza.priority.config.RuleWeights;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DueDateRule implements PriorityRule {

    @Override
    public int evaluate(Task task) {
        if (task.getDueDate() == null)  return 0;

        long days = ChronoUnit.DAYS.between(LocalDate.now(), task.getDueDate());

        // si ya esta vencida, la regla OverdueRule ya lo aplica
        // Aquí solo las que NO están vencidas pero vencen pronto
        if (days <= 0) return 0; // Ya lo maneja OverdueRule

        if (days <= 3) return RuleWeights.DUE_SOON;      // 30 pts
        if (days <= 7) return RuleWeights.DUE_MID;       // 15 pts
        if (days <= 14) return 5;                        // 5 pts (próximas 2 semanas)

        return 0;
    }
}
