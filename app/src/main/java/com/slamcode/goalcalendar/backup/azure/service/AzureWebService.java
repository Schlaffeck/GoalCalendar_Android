package com.slamcode.goalcalendar.backup.azure.service;

public class AzureWebService implements AzureService {

    private final AzureServiceConnection connection;

    public AzureWebService(AzureServiceConnection connection)
    {
        this.connection = connection;
    }

    @Override
    public void postBackupData(BackupData data) {

    }
}
