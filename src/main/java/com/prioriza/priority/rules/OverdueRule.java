package com.prioriza.priority.rules;

import com.prioriza.model.Task;
import com.prioriza.priority.config.RuleWeights;

import java.time.LocalDate;

public class OverdueRule implements PriorityRule{

    @Override
    public int evaluate(Task task){
        if(task.getDueDate() == null) return 0;

        return task.getDueDate().isBefore(LocalDate.now()) ? RuleWeights.OVERDUE : 0;
    }
}
