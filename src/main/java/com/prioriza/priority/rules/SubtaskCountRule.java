package com.prioriza.priority.rules;

import com.prioriza.dao.SubTaskDAO;
import com.prioriza.model.Task;
import com.prioriza.priority.config.RuleWeights;

public class SubtaskCountRule implements PriorityRule {

    //hay que acceder a la BD
    private final SubTaskDAO subTaskDAO = new SubTaskDAO();

    @Override
    public int evaluate(Task task){
        if(task == null) return 0;
        //cargar subtareas desde la BD si no están cargadas
        if(task.getSubTasks() == null || task.getSubTasks().isEmpty()){
            task.setSubTasks(subTaskDAO.getByTaskId(task.getId()));
        }
        int subTaskCount = task.getSubTasks() != null ? task.getSubTasks().size() : 0;

        //penalización por muchas subtareas pendientes
        long pendingSubtasks = task.getSubTasks().stream().filter(st -> st.getSubTaskStatus() != null &&
                st.getSubTaskStatus().name().equals("COMPLETA")).count();


        //más peso si hay muchas subtareas PENDIENTES
        if(pendingSubtasks >= 3){
            return RuleWeights.MANY_SUBTASKS * 2; // 20 puntos
        }else if(subTaskCount >= 3){
            return RuleWeights.MANY_SUBTASKS; // 10 puntos
        }
        return 0;
    }
}
