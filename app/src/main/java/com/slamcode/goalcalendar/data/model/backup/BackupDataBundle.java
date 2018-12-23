package com.slamcode.goalcalendar.data.model.backup;

import com.slamcode.goalcalendar.data.DataBundleAbstract;

import java.util.ArrayList;
import java.util.List;

public class BackupDataBundle extends DataBundleAbstract {

    public BackupDataBundle()
    {
        this.sources = new ArrayList<>();
        this.backupInfos = new ArrayList<>();
    }

    private List<BackupSourceModel> sources;

    private List<BackupInfoModel> backupInfos;

    public List<BackupSourceModel> getSources() {
        return sources;
    }

    public void setSources(List<BackupSourceModel> sources) {
        this.sources = sources;
    }

    public List<BackupInfoModel> getBackupInfos() {
        return backupInfos;
    }

    public void setBackupInfos(List<BackupInfoModel> backupInfos) {
        this.backupInfos = backupInfos;
    }
}
