package com.slamcode.goalcalendar.data.dagger2;

import android.content.Context;

import com.slamcode.goalcalendar.data.BackupPersistenceContext;
import com.slamcode.goalcalendar.data.DataFormatter;
import com.slamcode.goalcalendar.data.MainPersistenceContext;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.inmemory.DefaultModelInfoProvider;
import com.slamcode.goalcalendar.data.json.BackupJsonFilePersistenceContext;
import com.slamcode.goalcalendar.data.json.JsonFilePersistenceContext;
import com.slamcode.goalcalendar.data.json.JsonMonthlyPlansDataBundle;
import com.slamcode.goalcalendar.data.json.MainJsonFilePersistenceContext;
import com.slamcode.goalcalendar.data.json.formatter.JsonDataFormatter;
import com.slamcode.goalcalendar.data.model.ModelInfoProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by moriasla on 04.01.2017.
 */
@Module
public final class DataDagger2Module {

    private final static String DEFAULT_MAIN_DATA_FILE = "data.json";
    private final static String DEFAULT_BACKUP_DATA_FILE = "backup.json";

    private final Context context;

    public DataDagger2Module(Context context)
    {
        this.context = context;
    }

    @Provides
    @Singleton
    public DataFormatter<JsonMonthlyPlansDataBundle> provideJsonDataFormatter()
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
    public PersistenceContext providePersistenceContext(DataFormatter<JsonMonthlyPlansDataBundle> dataFormatter)
    {
        // create and return proper persistence context used in app
        return new MainJsonFilePersistenceContext(this.context, dataFormatter, DEFAULT_MAIN_DATA_FILE);
    }

    @Provides
    @Singleton
    public BackupPersistenceContext provideBackupPersistenceContext(DataFormatter<JsonMonthlyPlansDataBundle> dataFormatter)
    {
        // create and return proper persistence context used in app
        return new BackupJsonFilePersistenceContext(this.context, dataFormatter, DEFAULT_BACKUP_DATA_FILE);
    }
}
