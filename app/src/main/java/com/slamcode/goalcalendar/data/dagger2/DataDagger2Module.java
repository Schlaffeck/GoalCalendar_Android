package com.slamcode.goalcalendar.data.dagger2;

import android.content.Context;

import com.slamcode.goalcalendar.data.BackupPersistenceContext;
import com.slamcode.goalcalendar.data.DataFormatter;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.inmemory.DefaultModelInfoProvider;
import com.slamcode.goalcalendar.data.json.BackupJsonFilePersistenceContext;
import com.slamcode.goalcalendar.data.json.JsonPersistenceContextConfiguration;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;
import com.slamcode.goalcalendar.data.json.MainJsonFilePersistenceContext;
import com.slamcode.goalcalendar.data.json.formatter.JsonDataFormatter;
import com.slamcode.goalcalendar.data.model.ModelInfoProvider;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by moriasla on 04.01.2017.
 */
@Module
public final class DataDagger2Module {
    private final Context context;

    public DataDagger2Module(Context context)
    {
        this.context = context;
    }

    @Provides
    @Singleton
    public DataFormatter provideJsonDataFormatter()
    {
        return new JsonDataFormatter();
    }

    @Provides
    @Singleton
    public ModelInfoProvider provideModelInfoProvider()
    {
        return new DefaultModelInfoProvider();
    }

    @Provides
    @Singleton
    @Named(BackupJsonFilePersistenceContext.CONFIGURATION_NAME)
    public JsonPersistenceContextConfiguration getBackupJsonContextConfiguration()
    {
        return new JsonPersistenceContextConfiguration("b_data.json", "b_backup.json");
    }

    @Provides
    @Singleton
    @Named(MainJsonFilePersistenceContext.CONFIGURATION_NAME)
    public JsonPersistenceContextConfiguration getMainJsonContextConfiguration()
    {
        return new JsonPersistenceContextConfiguration("data.json", "backup.json");
    }

    @Provides
    @Singleton
    public PersistenceContext providePersistenceContext(DataFormatter dataFormatter, @Named(MainJsonFilePersistenceContext.CONFIGURATION_NAME) JsonPersistenceContextConfiguration configuration)
    {
        // create and return proper persistence context used in app
        return new MainJsonFilePersistenceContext(this.context, dataFormatter, configuration);
    }

    @Provides
    @Singleton
    public BackupPersistenceContext provideBackupPersistenceContext(DataFormatter dataFormatter, @Named(BackupJsonFilePersistenceContext.CONFIGURATION_NAME) JsonPersistenceContextConfiguration configuration)
    {
        // create and return proper persistence context used in app
        return new BackupJsonFilePersistenceContext(this.context, dataFormatter, configuration);
    }
}
