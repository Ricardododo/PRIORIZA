package com.prioriza.rule.rules;

import com.prioriza.model.Priority;
import com.prioriza.model.Task;
import com.prioriza.rule.RuleEngine;

import java.time.LocalDate;

public class TestRuleEngine {

    public static void main(String[] args) {

        Task task =  new Task(
                "Entrega DAM",
                "Proyecto final",
                LocalDate.now().plusDays(1),
                1
        );

        task.setPriority(Priority.HIGH);

        RuleEngine engine = new RuleEngine();

        engine.addRule(new UrgentRule());
        engine.addRule(new ImportantRule());
        engine.addRule(new DistantRule());

        int result = engine.evaluate(task);

        System.out.println("Prioridad calculada: " + result);

    }
}
