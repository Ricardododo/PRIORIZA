package com.prioriza.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationStats {
    private static final NotificationStats instance = new NotificationStats();

    // Estadísticas
    private final AtomicInteger totalScans = new AtomicInteger(0);
    private final AtomicInteger totalNotifications = new AtomicInteger(0);
    private final AtomicInteger totalEmailsSent = new AtomicInteger(0);
    private final AtomicInteger totalEmailsFailed = new AtomicInteger(0);
    private final AtomicLong lastScanTime = new AtomicLong(0);
    private LocalDate lastScanDate = LocalDate.now();
    private int scansToday = 0;

    private NotificationStats() {}

    public static NotificationStats getInstance() {
        return instance;
    }

    // Registrar escaneo
    public void registerScan() {
        totalScans.incrementAndGet();
        lastScanTime.set(System.currentTimeMillis());

        // Resetear contador diario si es nuevo día
        LocalDate today = LocalDate.now();
        if (!today.equals(lastScanDate)) {
            scansToday = 0;
            lastScanDate = today;
        }
        scansToday++;
    }

    // Registrar notificación creada
    public void registerNotification() {
        totalNotifications.incrementAndGet();
    }

    // Registrar email enviado
    public void registerEmailSent() {
        totalEmailsSent.incrementAndGet();
    }

    //Registrar email fallido
    public void registerEmailFailed() {
        totalEmailsFailed.incrementAndGet();
    }

    // Getters
    public int getTotalScans() { return totalScans.get(); }
    public int getScansToday() { return scansToday; }
    public int getTotalNotifications() { return totalNotifications.get(); }
    public int getTotalEmailsSent() { return totalEmailsSent.get(); }
    public int getTotalEmailsFailed() { return totalEmailsFailed.get(); }
    public long getLastScanTime() { return lastScanTime.get(); }

    // Reporte completo
    public String getReport() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        return String.format(
                """
                \nESTADÍSTICAS DE NOTIFICACIONES
                ═══════════════════════════════════
                Fecha: %s
                Último escaneo: %s
                Escaneos totales: %d
                Escaneos hoy: %d
                Notificaciones creadas: %d
                Emails enviados: %d
                Emails fallidos: %d
                Tasa de éxito: %.1f%%
                ═══════════════════════════════════
                """,
                LocalDate.now(),
                lastScanTime.get() > 0 ?
                        java.time.LocalTime.ofInstant(java.time.Instant.ofEpochMilli(lastScanTime.get()),
                                java.time.ZoneId.systemDefault()).format(timeFormatter) : "Nunca",
                totalScans.get(),
                scansToday,
                totalNotifications.get(),
                totalEmailsSent.get(),
                totalEmailsFailed.get(),
                totalEmailsSent.get() + totalEmailsFailed.get() > 0 ?
                        (totalEmailsSent.get() * 100.0) / (totalEmailsSent.get() + totalEmailsFailed.get()) : 0
        );
    }
}
