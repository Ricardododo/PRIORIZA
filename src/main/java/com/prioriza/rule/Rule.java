package com.prioriza.rule;

import com.prioriza.model.Task;

public interface Rule {
    /*evalúa una tarea y devulve un valor heurístico
    que afectará a su prioridad
     */

    int evaluate(Task task);
}
