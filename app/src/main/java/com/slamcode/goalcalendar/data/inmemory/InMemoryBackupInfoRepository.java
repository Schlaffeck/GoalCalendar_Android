package com.slamcode.goalcalendar.data.inmemory;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementSelector;
import com.slamcode.collections.Predicate;
import com.slamcode.goalcalendar.data.BackupInfoRepository;
import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class InMemoryBackupInfoRepository extends InMemoryRepositoryBase<BackupInfoModel, UUID> implements BackupInfoRepository {

    private final List<BackupInfoModel> backupInfos;

    public InMemoryBackupInfoRepository(List<BackupInfoModel> backupInfos) {
        this.backupInfos = backupInfos;
    }

    @Override
    public BackupInfoModel getLatestBackupInfo() {
        return CollectionUtils.firstMax(this.backupInfos, new ElementSelector<BackupInfoModel, Date>() {
            @Override
            public Date select(BackupInfoModel parent) {
                return parent.getBackupDateUtc();
            }
        });
    }

    @Override
    public BackupInfoModel getLatestBackupInfo(final String sourceType) {
        return CollectionUtils.firstMax(
                CollectionUtils.filter(this.backupInfos, new Predicate<BackupInfoModel>() {
                    @Override
                    public boolean apply(BackupInfoModel item) {
                        return item != null && sourceType.equals(item.getSourceType());
                    }
                }),
                new ElementSelector<BackupInfoModel, Date>() {
                    @Override
                    public Date select(BackupInfoModel parent) {
                        return parent.getSourceType().equals(sourceType) ? parent.getBackupDateUtc() : null;
                    }
                });
    }
}
