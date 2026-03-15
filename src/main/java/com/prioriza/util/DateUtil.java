package com.prioriza.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utilidades para manejo de fechas y tiempos.
 * 
 * Proporciona métodos estáticos para formateo, parsing y conversión
 * de fechas y horas en diferentes formatos.
 * 
 * @author PRIORIZA
 * @version 1.0
 */
public class DateUtil {

    // FORMATOS
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");        // Para mostrar fecha
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");  // Para mostrar fecha+hora
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");             // Para mostrar hora
    private static final DateTimeFormatter FILE_NAME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");   // Para nombres de archivo
    private static final DateTimeFormatter SQL_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Para guardar en BD

    //PARSE
    public static LocalTime parseTime(String timeStr) throws DateTimeParseException {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        return LocalTime.parse(timeStr.trim(), TIME_FORMATTER);
    }

    // FORMATO PARA MOSTRAR
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_FORMATTER) : "";
    }

    public static String formatTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(TIME_FORMATTER) : "";
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : "";
    }

    // FORMATO PARA BASE DE DATOS
    public static String formatForDatabase(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(SQL_FORMATTER) : null;
    }

    // FORMATO PARA NOMBRES DE ARCHIVO
    public static String formatForFileName(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(FILE_NAME_FORMATTER) : "";
    }

    public static String formatForFileName(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) : "";
    }

    //CONVERSIONES ÚTILES
    public static LocalDateTime atStartOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay() : null;
    }

    public static LocalDateTime combine(LocalDate date, LocalTime time) {
        if (date == null) return null;
        if (time == null) return date.atStartOfDay();
        return LocalDateTime.of(date, time);
    }
}
