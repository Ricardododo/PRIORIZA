package com.prioriza.rule.rules;

import com.prioriza.model.Task;
import com.prioriza.rule.Rule;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class UrgentRule implements Rule {

    @Override
    public int evaluate(Task task) {

        if (task.getDueDate() == null) return 0;

        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), task.getDueDate());

        if (daysLeft <= 1) {
            return 10; //muy urgente
        }
        if (daysLeft <= 3) {
            return 7; //urgente
        }

        return 0;
    }
}
