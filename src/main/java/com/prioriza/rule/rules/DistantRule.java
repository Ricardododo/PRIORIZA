package com.prioriza.rule.rules;

import com.prioriza.model.Task;
import com.prioriza.rule.Rule;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DistantRule implements Rule {

    @Override
    public int evaluate(Task task) {

        if(task.getDueDate() == null) return 0;

        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), task.getDueDate());

        if (daysLeft > 30){
            return -10; //nada urgente
        }
        if (daysLeft > 10){
            return -2; // un poco urgente
        }

        return 0;
    }
}
