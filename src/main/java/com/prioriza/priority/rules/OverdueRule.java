package com.prioriza.priority.rules;

import com.prioriza.model.Task;
import com.prioriza.priority.config.RuleWeights;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OverdueRule implements PriorityRule{

    @Override
    public int evaluate(Task task){
        if(task.getDueDateTime() == null) return 0;

        LocalDateTime now = LocalDateTime.now();

        return task.getDueDateTime().isBefore(now) ? RuleWeights.OVERDUE : 0;
    }
}

