package com.prioriza.model;

public class UserNotificationSettings {
    private int userId;
    private boolean emailEnabled = true;
    private int daysBeforeAlert = 1; // Días de anticipación
    private boolean alertForSubtasks = true;
    private boolean alertOnlyWorkingDays = false;
    private int maxAlertsPerDay = 5;

    public UserNotificationSettings() {
    }

    public UserNotificationSettings(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public int getDaysBeforeAlert() {
        return daysBeforeAlert;
    }

    public void setDaysBeforeAlert(int daysBeforeAlert) {
        this.daysBeforeAlert = daysBeforeAlert;
    }

    public boolean isAlertForSubtasks() {
        return alertForSubtasks;
    }

    public void setAlertForSubtasks(boolean alertForSubtasks) {
        this.alertForSubtasks = alertForSubtasks;
    }

    public boolean isAlertOnlyWorkingDays() {
        return alertOnlyWorkingDays;
    }

    public void setAlertOnlyWorkingDays(boolean alertOnlyWorkingDays) {
        this.alertOnlyWorkingDays = alertOnlyWorkingDays;
    }

    public int getMaxAlertsPerDay() {
        return maxAlertsPerDay;
    }

    public void setMaxAlertsPerDay(int maxAlertsPerDay) {
        this.maxAlertsPerDay = maxAlertsPerDay;
    }
}
