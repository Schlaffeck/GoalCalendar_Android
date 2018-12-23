package com.slamcode.goalcalendar.backup.impl;

import com.slamcode.goalcalendar.backup.BackupSourceDataProvider;
import com.slamcode.goalcalendar.backup.BackupSourceDataProvidersRegistry;

import java.util.Collection;
import java.util.Map;

public class DefaultBackupSourceDataProvidersRegistry implements BackupSourceDataProvidersRegistry {

    private final Map<String, BackupSourceDataProvider> providerMap;

    public DefaultBackupSourceDataProvidersRegistry(Map<String, BackupSourceDataProvider> providerMap) {

        this.providerMap = providerMap;
    }

    @Override
    public Collection<BackupSourceDataProvider> getProviders() {
        return this.providerMap.values();
    }

    @Override
    public Collection<String> getProviderTypes() {
        return this.providerMap.keySet();
    }

    @Override
    public BackupSourceDataProvider getProviderByType(String type) {

        if(!this.providerMap.containsKey(type))
            return null;

        return this.providerMap.get(type);
    }
}
