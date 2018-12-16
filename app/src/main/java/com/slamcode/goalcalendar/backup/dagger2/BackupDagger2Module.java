package com.slamcode.goalcalendar.backup.dagger2;

import android.content.Context;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.DefaultApplicationContext;
import com.slamcode.goalcalendar.backup.BackupWriter;
import com.slamcode.goalcalendar.backup.local.PersistenceContextBackupReaderWriter;
import com.slamcode.goalcalendar.data.BackupPersistenceContext;
import com.slamcode.goalcalendar.data.MainPersistenceContext;
import com.slamcode.goalcalendar.data.model.ModelInfoProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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
    BackupWriter provideBackupWriter(ModelInfoProvider modelInfoProvider, MainPersistenceContext mainPersistenceContext, BackupPersistenceContext backupPersistenceContext)
    {
        return new PersistenceContextBackupReaderWriter(modelInfoProvider, mainPersistenceContext, backupPersistenceContext);
    }
}
