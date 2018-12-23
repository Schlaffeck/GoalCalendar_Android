package com.slamcode.goalcalendar.backup;

import java.util.Collection;

public interface BackupSourceDataProvidersRegistry {

    Collection<BackupSourceDataProvider> getProviders();

    Collection<String> getProviderTypes();

    BackupSourceDataProvider getProviderByType(String type);
}
