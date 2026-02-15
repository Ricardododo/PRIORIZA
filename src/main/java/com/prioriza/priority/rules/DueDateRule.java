package com.prioriza.priority.rules;

import com.prioriza.model.Task;
import com.prioriza.priority.config.RuleWeights;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DueDateRule implements PriorityRule {

    @Override
    public int evaluate(Task task) {
        if (task.getDueDateTime() == null)  return 0;

        LocalDateTime now = LocalDateTime.now();
        long hoursUntil = ChronoUnit.HOURS.between(now, task.getDueDateTime());

        if (hoursUntil <= 3) return 40;      // Próximas 3 horas
        if (hoursUntil <= 8) return 30;      // Hoy
        if (hoursUntil <= 24) return 20;     // Mañana
        if (hoursUntil <= 72) return 10;     // Próximos 3 días

        return 0;
    }
}
