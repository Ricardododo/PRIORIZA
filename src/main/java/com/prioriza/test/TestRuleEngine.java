package com.prioriza.test;

import com.prioriza.model.Priority;
import com.prioriza.model.Task;
import com.prioriza.rule.RuleEngine;
import com.prioriza.rule.rules.DistantRule;
import com.prioriza.rule.rules.ImportantRule;
import com.prioriza.rule.rules.UrgentRule;

import java.time.LocalDate;

public class TestRuleEngine {

    public static void main(String[] args) {

        Task task =  new Task(
                "Entrega DAM",
                "Proyecto final",
                LocalDate.now().plusDays(1),
                1
        );

        task.setPriority(Priority.ALTA);

        RuleEngine engine = new RuleEngine();

        engine.addRule(new UrgentRule());
        engine.addRule(new ImportantRule());
        engine.addRule(new DistantRule());

        int result = engine.evaluate(task);

        System.out.println("Prioridad calculada: " + result);

    }
}
