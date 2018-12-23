package com.slamcode.goalcalendar.backup.impl;

import com.slamcode.goalcalendar.backup.BackupSourceDataProvider;
import com.slamcode.goalcalendar.backup.BackupSourceDataProvidersRegistry;

import java.util.Collection;

public class DefaultBackupSourceDataProvidersRegistry implements BackupSourceDataProvidersRegistry {
    @Override
    public Collection<BackupSourceDataProvider> getProviders() {
        return null;
    }

    @Override
    public Collection<String> getProviderTypes() {
        return null;
    }

    @Override
    public BackupSourceDataProvider getProviderByType(String type) {
        return null;
    }
}
