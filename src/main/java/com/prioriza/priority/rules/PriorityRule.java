package com.prioriza.priority.rules;

import com.prioriza.model.Task;

public interface PriorityRule {
    int evaluate(Task task);
}
