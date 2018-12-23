package com.slamcode.goalcalendar.backup.dagger2;

import android.content.Context;

import com.slamcode.goalcalendar.backup.BackupSourceDataProvider;
import com.slamcode.goalcalendar.backup.BackupSourceDataProvidersRegistry;
import com.slamcode.goalcalendar.backup.BackupWriter;
import com.slamcode.goalcalendar.backup.impl.DefaultBackupSourceDataProvidersRegistry;
import com.slamcode.goalcalendar.backup.local.PersistenceContextBackupReaderRestorer;
import com.slamcode.goalcalendar.backup.local.PersistenceContextBackupSourceDataProvider;
import com.slamcode.goalcalendar.data.BackupPersistenceContext;
import com.slamcode.goalcalendar.data.MainPersistenceContext;
import com.slamcode.goalcalendar.data.model.ModelInfoProvider;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.StringKey;

/**
 * Created by moriasla on 01.03.2017.
 */
@Module
public class BackupDagger2Module {

    private final Context context;

    public BackupDagger2Module(Context context)
    {
        this.context = context;
    }

    @Singleton
    @Provides
    PersistenceContextBackupReaderRestorer provideLocalBackupWriter(ModelInfoProvider modelInfoProvider, MainPersistenceContext mainPersistenceContext, BackupPersistenceContext backupPersistenceContext)
    {
        return new PersistenceContextBackupReaderRestorer(modelInfoProvider, mainPersistenceContext, backupPersistenceContext);
    }

    @Singleton
    @Provides(type = Provides.Type.MAP)
    @StringKey(PersistenceContextBackupSourceDataProvider.SOURCE_TYPE)
    BackupSourceDataProvider provideLocalBackupSource(AppSettingsManager appSettingsManager, PersistenceContextBackupReaderRestorer readerWriter)
    {
        return new PersistenceContextBackupSourceDataProvider(appSettingsManager, readerWriter);
    }

    @Singleton
    @Provides
    BackupSourceDataProvidersRegistry provideBackupSourceProvidersRegistry(Map<String, BackupSourceDataProvider> providerMap)
    {
        return new DefaultBackupSourceDataProvidersRegistry(providerMap);
    }
}
