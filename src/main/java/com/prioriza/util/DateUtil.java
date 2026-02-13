package com.prioriza.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("ddMMyyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("ddMMyyyy HH:mm");
    private static final DateTimeFormatter FILE_NAME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : "";
    }

    public static String formatForFileName(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(FILE_NAME_FORMATTER) : "";
    }

    public static String formatForFileName(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) : "";
    }
}
