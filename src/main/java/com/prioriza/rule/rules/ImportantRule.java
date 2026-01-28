package com.prioriza.rule.rules;

import com.prioriza.model.Task;
import com.prioriza.rule.Rule;

public class ImportantRule implements Rule {

    @Override
    public int evaluate(Task task) {

        if(task.getPriority() == null) return 0;

        return switch (task.getPriority()){
            case URGENT -> 10;
            case HIGH -> 5;
            case MEDIUM -> 3;
            case LOW -> 1;
        };

    }
}
