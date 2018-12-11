package com.slamcode.goalcalendar.data.model.backup;

import com.slamcode.goalcalendar.data.Identifiable;

import java.util.Date;
import java.util.UUID;

public class BackupInfoModel implements Identifiable<UUID> {

    private UUID id;

    private int version;

    private Date backupDateUtc;

    private String sourceType;

    public BackupInfoModel()
    {
        this.id = UUID.randomUUID();
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getBackupDateUtc() {
        return backupDateUtc;
    }

    public void setBackupDateUtc(Date backupDateUtc) {
        this.backupDateUtc = backupDateUtc;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
}
