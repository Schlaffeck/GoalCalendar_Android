package com.slamcode.goalcalendar.viewmodels;

import android.databinding.BaseObservable;

import com.slamcode.goalcalendar.backup.BackupSourceDataProvider;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.view.SourceChangeRequestNotifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BackupSourceViewModel extends BaseObservable implements SourceChangeRequestNotifier<BackupSourceViewModel> {

    public static final int BACKUP_SOURCE_CREATE_BACKUP_REQUEST = 6723;
    public static final int BACKUP_SOURCE_RESTORE_BACKUP_REQUEST = 6724;

    private final BackupSourceDataProvider dataProvider;
    private final DateTime lastBackupDateTime;

    private List<SourceChangeRequestListener<BackupSourceViewModel>> sourceChangeRequestListeners;

    BackupSourceViewModel(BackupSourceDataProvider dataProvider, DateTime lastBackupDateTime)
    {
        this.dataProvider = dataProvider;
        this.lastBackupDateTime = lastBackupDateTime;
        this.sourceChangeRequestListeners = new ArrayList<>();
    }

    public String getName() {
        return this.dataProvider.getDisplayData(Locale.getDefault()).getSourceName();
    }

    public boolean isHasBackup() {
        return this.lastBackupDateTime != null;
    }

    public DateTime getLastBackupDateTime()
    {
        return this.lastBackupDateTime;
    }

    public String getSourceType()
    {
        return this.dataProvider.getSourceType();
    }

    @Override
    public void addSourceChangeRequestListener(SourceChangeRequestListener<BackupSourceViewModel> listener) {
        if(listener != null
                && !this.sourceChangeRequestListeners.contains(listener))
            this.sourceChangeRequestListeners.add(listener);
    }

    @Override
    public void removeSourceChangeRequestListener(SourceChangeRequestListener<BackupSourceViewModel> listener) {
        this.sourceChangeRequestListeners.remove(listener);
    }

    @Override
    public void clearSourceChangeRequestListeners() {
        this.sourceChangeRequestListeners.clear();
    }

    @Override
    public void notifySourceChangeRequested(SourceChangeRequest request) {
        for (SourceChangeRequestListener<BackupSourceViewModel> listener : this.sourceChangeRequestListeners)
        {
            listener.sourceChangeRequested(this, request);
        }
    }
}
