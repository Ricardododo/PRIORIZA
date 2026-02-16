package com.prioriza.model;

public class UserSettings {
    private int userId;
    private boolean emailEnabled;
    private int daysBeforeAlert;
    private boolean alertForSubtasks;
    private boolean alertOnlyWorkingDays;
    private int maxAlertsPerDay;
    private int notificationHour; // 0-23

    // Constructor vacío JDBC
    public UserSettings() {}

    // Constructor con valores por defecto
    public UserSettings(int userId) {
        this.userId = userId;
        this.emailEnabled = true;
        this.daysBeforeAlert = 1;
        this.alertForSubtasks = true;
        this.alertOnlyWorkingDays = false;
        this.maxAlertsPerDay = 5;
        this.notificationHour = 9;
    }
    // Constructor completo
    public UserSettings(int userId, boolean emailEnabled, int daysBeforeAlert,
                        boolean alertForSubtasks, boolean alertOnlyWorkingDays,
                        int maxAlertsPerDay, int notificationHour) {
        this.userId = userId;
        this.emailEnabled = emailEnabled;
        this.daysBeforeAlert = daysBeforeAlert;
        this.alertForSubtasks = alertForSubtasks;
        this.alertOnlyWorkingDays = alertOnlyWorkingDays;
        this.maxAlertsPerDay = maxAlertsPerDay;
        this.notificationHour = notificationHour;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }

    public int getDaysBeforeAlert() { return daysBeforeAlert; }
    public void setDaysBeforeAlert(int daysBeforeAlert) { this.daysBeforeAlert = daysBeforeAlert; }

    public boolean isAlertForSubtasks() { return alertForSubtasks; }
    public void setAlertForSubtasks(boolean alertForSubtasks) { this.alertForSubtasks = alertForSubtasks; }

    public boolean isAlertOnlyWorkingDays() { return alertOnlyWorkingDays; }
    public void setAlertOnlyWorkingDays(boolean alertOnlyWorkingDays) { this.alertOnlyWorkingDays = alertOnlyWorkingDays; }

    public int getMaxAlertsPerDay() { return maxAlertsPerDay; }
    public void setMaxAlertsPerDay(int maxAlertsPerDay) { this.maxAlertsPerDay = maxAlertsPerDay; }

    public int getNotificationHour() { return notificationHour; }
    public void setNotificationHour(int notificationHour) { this.notificationHour = notificationHour; }

    //--------métodos útiles
    //aplica configuración a un detector de notificaciones
    public void applyToDetector() {
        // Aquí puedes poner lógica para aplicar la configuración
        System.out.println("Configuración aplicada: " + this);
    }
    //crear copia de esta configuración
    public UserSettings copy() {
        return new UserSettings(
                userId, emailEnabled, daysBeforeAlert, alertForSubtasks,
                alertOnlyWorkingDays, maxAlertsPerDay, notificationHour
        );
    }


    @Override
    public String toString() {
        return String.format(
                "UserSettings[userId=%d, email=%b, days=%d, subtasks=%b, workingDays=%b, max=%d, hour=%d]",
                userId, emailEnabled, daysBeforeAlert, alertForSubtasks,
                alertOnlyWorkingDays, maxAlertsPerDay, notificationHour
        );
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSettings that = (UserSettings) o;
        return userId == that.userId &&
                emailEnabled == that.emailEnabled &&
                daysBeforeAlert == that.daysBeforeAlert &&
                alertForSubtasks == that.alertForSubtasks &&
                alertOnlyWorkingDays == that.alertOnlyWorkingDays &&
                maxAlertsPerDay == that.maxAlertsPerDay &&
                notificationHour == that.notificationHour;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(userId, emailEnabled, daysBeforeAlert,
                alertForSubtasks, alertOnlyWorkingDays, maxAlertsPerDay, notificationHour);
    }

}


