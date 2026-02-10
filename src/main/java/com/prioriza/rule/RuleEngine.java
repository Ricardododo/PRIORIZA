package com.prioriza.rule;

import com.prioriza.model.Task;

import java.util.ArrayList;
import java.util.List;

public class RuleEngine {

    //lista de reglas
    private final List<Rule> rules = new ArrayList<>();

    //añadir reglas al motor
    public void addRule(Rule rule){
        rules.add(rule);
    }

    //evaluar una tarea usando todas las reglas
    public int evaluate(Task task) {
        int priority = 0; //arranca desde cero

        System.out.println("==== EVALUANDO TAREA ====");
        System.out.println("Título: " + task.getTitle());
        System.out.println("Fecha: " + task.getDueDate());

        for (Rule rule : rules){
            int ruleScore = rule.evaluate(task);
            System.out.println(rule.getClass().getSimpleName() + " -> " + ruleScore);
            priority += ruleScore;
        }

        System.out.println("SCORE FINAL: " + priority);
        System.out.println("=========================");

        return priority;
    }
}
