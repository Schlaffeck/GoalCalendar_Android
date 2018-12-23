package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;

import com.slamcode.goalcalendar.backup.BackupSourceDataProvider;
import com.slamcode.goalcalendar.backup.BackupSourceDataProvidersRegistry;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;
import com.slamcode.goalcalendar.data.unitofwork.UnitOfWork;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class BackupViewModel extends BaseObservable {

    private final PersistenceContext mainPersistenceContext;
    private final BackupSourceDataProvidersRegistry backupSourceDataProvidersRegistry;

    private ObservableArrayList<BackupSourceViewModel> backupSources;

    public BackupViewModel(PersistenceContext mainPersistenceContext, BackupSourceDataProvidersRegistry backupSourceDataProvidersRegistry)
    {
        this.mainPersistenceContext = mainPersistenceContext;
        this.backupSourceDataProvidersRegistry = backupSourceDataProvidersRegistry;
        this.backupSources = new ObservableArrayList<>();
    }

    public ObservableArrayList<BackupSourceViewModel> getBackupSources()
    {
        if(this.backupSources.isEmpty())
        {
            this.prepareBackupSourcesData();
        }

        return this.backupSources;
    }

    private void prepareBackupSourcesData() {
        this.backupSources.clear();
        for (BackupSourceDataProvider provider : this.backupSourceDataProvidersRegistry.getProviders())
        {
            this.backupSources.add(new BackupSourceViewModel(provider, this.readLastBackupDate(provider)));
        }
    }

    private DateTime readLastBackupDate(BackupSourceDataProvider provider)
    {
        UnitOfWork uow = this.mainPersistenceContext.createUnitOfWork(true);
        BackupInfoModel lastInfo = uow.getBackupInfoRepository().getLatestBackupInfo(provider.getSourceType());
        uow.complete();

        return DateTimeHelper.getDateTime(lastInfo.getBackupDateUtc().getTime());
    }
}
