package com.prioriza.model;

public class UserSettings {
    private int userId;
    private boolean emailEnabled;
    private int daysBeforeAlert;
    private boolean alertForSubtasks;
    private boolean alertOnlyWorkingDays;
    private int maxAlertsPerDay;
    private int notificationHour; // 0-23

    // Constructor vacío
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

    @Override
    public String toString() {
        return String.format(
                "UserSettings[userId=%d, email=%b, days=%d, subtasks=%b, workingDays=%b, max=%d, hour=%d]",
                userId, emailEnabled, daysBeforeAlert, alertForSubtasks,
                alertOnlyWorkingDays, maxAlertsPerDay, notificationHour);
    }
}


