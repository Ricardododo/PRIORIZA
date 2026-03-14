package com.prioriza.priority.model;

/**
 * Enum representing priority levels calculated by the priority engine.
 * 
 * These levels are used internally by the priority calculation system.
 * Order: CRITICO > URGENTE > ALTO > MEDIO > BAJO
 * 
 * @author PRIORIZA
 * @version 1.0
 */
public enum PriorityLevel {
    CRITICO,
    URGENTE,
    ALTO,
    MEDIO,
    BAJO
}
