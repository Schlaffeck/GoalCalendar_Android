package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;

import com.android.databinding.library.baseAdapters.BR;
import com.slamcode.goalcalendar.backup.BackupSourceDataProvider;
import com.slamcode.goalcalendar.backup.BackupSourceDataProvidersRegistry;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;
import com.slamcode.goalcalendar.data.unitofwork.UnitOfWork;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;

public class BackupViewModel extends BaseObservable {

    private final PersistenceContext mainPersistenceContext;
    private final BackupSourceDataProvidersRegistry backupSourceDataProvidersRegistry;

    private ObservableArrayList<BackupSourceViewModel> backupSources;
    private boolean processingList;

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

    @Bindable
    public boolean isProcessingList()
    {
        return this.processingList;
    }

    public void setProcessingList(boolean newValue)
    {
        if(this.processingList != newValue) {
            this.processingList = newValue;
            this.notifyPropertyChanged(BR.processingList);
        }
    }

    private void prepareBackupSourcesData() {
        this.backupSources.clear();
        for (BackupSourceDataProvider provider : this.backupSourceDataProvidersRegistry.getProviders())
        {
            BackupSourceViewModel viewModel = new BackupSourceViewModel(provider, this.readLastBackupDate(provider));
            this.backupSources.add(viewModel);
        }
    }

    private DateTime readLastBackupDate(BackupSourceDataProvider provider)
    {
        UnitOfWork uow = this.mainPersistenceContext.createUnitOfWork(true);
        BackupInfoModel lastInfo = uow.getBackupInfoRepository().getLatestBackupInfo(provider.getSourceType());
        uow.complete();

        return lastInfo != null ? DateTimeHelper.getDateTime(lastInfo.getBackupDateUtc().getTime()) : null;
    }
}
