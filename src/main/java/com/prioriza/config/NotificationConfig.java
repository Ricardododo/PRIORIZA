package com.prioriza.config;

import java.time.LocalTime;

public class NotificationConfig {

    //Horarios entre las 8 am y las 10 pm cada 2 horas
    public static final int SCAN_START_HOUR = 8;
    public static final int SCAN_END_HOUR = 22;
    public static final int SCAN_INTERVAL_HOURS = 2;

    public static final int SEND_INTERVAL_MINUTES = 15; //cada 15 minutos

    //Límites
    public static final int MAX_SCANS_PER_DAY = 6; //Máximo 6 escaneos/día
    public static final int MAX_EMAILS_PER_SCAN = 50; //Máx 50 emails por envío
    public static final int MAX_EMAILS_PER_DAY = 300; //Máx 300 email/día

    //días de alerta
    public static final int[] ALERT_DAYS = {1, 2, 3, 5, 7}; //Días antes de vencer

    //modo desarrollo
    public static final boolean DEV_MODE = true; //para producción sería false

    //Horarios especiales para Desarrollo
    public static LocalTime[] getDevSchedule() {
        if (DEV_MODE) {
            return new LocalTime[] {
                    LocalTime.now().plusMinutes(1),  // 1 minuto
                    LocalTime.now().plusMinutes(5),  // 5 minutos
                    LocalTime.now().plusMinutes(10)  // 10 minutos
            };
        }
        return new LocalTime[0];
    }

    // Verificar si está en horario laboral
    public static boolean isWorkingHours() {
        if (DEV_MODE) return true; // En desarrollo, siempre true

        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        return hour >= SCAN_START_HOUR && hour <= SCAN_END_HOUR;
    }

    // Obtener mensaje de estado
    public static String getStatusMessage() {
        return String.format(
                "CONFIGURACIÓN:\n" +
                        "   Modo: %s\n" +
                        "   Horario: %d:00 - %d:00\n" +
                        "   Escaneo: cada %d horas\n" +
                        "   Envío: cada %d minutos\n" +
                        "   Alertas: %d días antes",
                DEV_MODE ? "DESARROLLO" : "PRODUCCIÓN",
                SCAN_START_HOUR, SCAN_END_HOUR,
                SCAN_INTERVAL_HOURS,
                SEND_INTERVAL_MINUTES,
                ALERT_DAYS.length
        );
    }
}
