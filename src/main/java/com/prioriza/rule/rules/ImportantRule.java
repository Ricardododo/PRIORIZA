package com.prioriza.rule.rules;

import com.prioriza.model.Task;
import com.prioriza.rule.Rule;

public class ImportantRule implements Rule {

    @Override
    public int evaluate(Task task) {

        if(task.getPriority() == null) return 0;

        return switch (task.getPriority()){
            case URGENTE -> 10;
            case ALTA -> 5;
            case MEDIA -> 3;
            case BAJA -> 1;
        };

    }
}
