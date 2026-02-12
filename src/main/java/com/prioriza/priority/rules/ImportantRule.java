package com.prioriza.priority.rules;

import com.prioriza.model.Task;
import com.prioriza.priority.config.RuleWeights;

public class ImportantRule implements PriorityRule{

    @Override
    public int evaluate(Task task){
        return task.isImportant() ? RuleWeights.IMPORTANT : 0;
    }
}
