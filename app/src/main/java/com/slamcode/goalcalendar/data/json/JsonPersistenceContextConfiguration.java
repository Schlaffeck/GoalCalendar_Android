package com.slamcode.goalcalendar.data.json;

public class JsonPersistenceContextConfiguration {

    private String plansDataFileName;

    private String backupInfoDataFileName;

    public JsonPersistenceContextConfiguration(String plansDataFileName, String backupInfoDataFileName) {
        this.plansDataFileName = plansDataFileName;
        this.backupInfoDataFileName = backupInfoDataFileName;
    }

    public String getPlansDataFileName() {
        return plansDataFileName;
    }

    public String getBackupInfoDataFileName() {
        return backupInfoDataFileName;
    }
}
