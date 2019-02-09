package com.slamcode.goalcalendar.backup.dagger2;

import android.content.Context;

import com.slamcode.goalcalendar.authentication.AuthenticationProvider;
import com.slamcode.goalcalendar.backup.BackupSourceDataProvider;
import com.slamcode.goalcalendar.backup.BackupSourceDataProvidersRegistry;
import com.slamcode.goalcalendar.backup.azure.AzureBackupSourceDataProvider;
import com.slamcode.goalcalendar.backup.azure.AzureBackupWriter;
import com.slamcode.goalcalendar.backup.azure.service.AzureService;
import com.slamcode.goalcalendar.backup.azure.service.AzureServiceConnection;
import com.slamcode.goalcalendar.backup.azure.service.AzureWebService;
import com.slamcode.goalcalendar.backup.impl.DefaultBackupSourceDataProvidersRegistry;
import com.slamcode.goalcalendar.backup.local.PersistenceContextBackupWriterRestorer;
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
    PersistenceContextBackupWriterRestorer provideLocalBackupWriter(ModelInfoProvider modelInfoProvider, MainPersistenceContext mainPersistenceContext, BackupPersistenceContext backupPersistenceContext)
    {
        return new PersistenceContextBackupWriterRestorer(modelInfoProvider, mainPersistenceContext, backupPersistenceContext);
    }

    @Singleton
    @Provides(type = Provides.Type.MAP)
    @StringKey(PersistenceContextBackupSourceDataProvider.SOURCE_TYPE)
    BackupSourceDataProvider provideLocalBackupSource(AppSettingsManager appSettingsManager, PersistenceContextBackupWriterRestorer readerWriter)
    {
        return new PersistenceContextBackupSourceDataProvider(appSettingsManager, readerWriter);
    }

    @Singleton
    @Provides
    AzureServiceConnection provideAzureApiServiceConnection()
    {
        // https://slamcodegoalcalendarfunctionapp.azurewebsites.net/api/backups/{version}/{id}?code=Z3i3yTOxNOGw/PenaP4Q4Pdl2DQSYnwYtui49zJfF0aCV1KdX9f9Pg==
        return new AzureServiceConnection("https://slamcodegoalcalendarfunctionapp.azurewebsites.net/", "code=Z3i3yTOxNOGw/PenaP4Q4Pdl2DQSYnwYtui49zJfF0aCV1KdX9f9Pg==");
    }

    @Singleton
    @Provides
    AzureService provideAzureService(AzureServiceConnection connection)
    {
        return new AzureWebService(connection);
    }

    @Singleton
    @Provides
    AzureBackupWriter provideAzureBackupWriter(AzureService service, ModelInfoProvider modelInfoProvider, MainPersistenceContext mainPersistenceContext)
    {
        return new AzureBackupWriter(service, modelInfoProvider, mainPersistenceContext);
    }

    @Singleton
    @Provides(type = Provides.Type.MAP)
    @StringKey(AzureBackupSourceDataProvider.SOURCE_TYPE)
    BackupSourceDataProvider provideAzureBackupSource(AuthenticationProvider authenticationProvider, AzureBackupWriter writer)
    {
        return new AzureBackupSourceDataProvider(authenticationProvider, writer);
    }

    @Singleton
    @Provides
    BackupSourceDataProvidersRegistry provideBackupSourceProvidersRegistry(Map<String, BackupSourceDataProvider> providerMap)
    {
        return new DefaultBackupSourceDataProvidersRegistry(providerMap);
    }
}
