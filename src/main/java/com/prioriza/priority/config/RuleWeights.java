package com.prioriza.priority.config;

public class RuleWeights {

    public static final int OVERDUE = 50;        // Vencida
    public static final int DUE_SOON = 30;       // Próximos 3 días
    public static final int DUE_MID = 15;        // Próximos 7 días
    public static final int DUE_LATER = 5;       // Próximos 14 días
    public static final int MANY_SUBTASKS = 10;  // ≥3 subtareas
    public static final int IMPORTANT = 20;      // Marcada como importante
    public static final int IN_PROGRESS = 15;    // En progreso
}
