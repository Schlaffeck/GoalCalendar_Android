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

    public InMemoryBackupInfoRepository(List<BackupInfoModel> backupInfos) {
        super(backupInfos);
    }

    @Override
    public BackupInfoModel getLatestBackupInfo() {
        return CollectionUtils.firstMax(this.getInMemoryEntityList(), new ElementSelector<BackupInfoModel, Date>() {
            @Override
            public Date select(BackupInfoModel parent) {
                return parent.getBackupDateUtc();
            }
        });
    }

    @Override
    public BackupInfoModel getLatestBackupInfo(final String sourceType) {
        return CollectionUtils.firstMax(
                CollectionUtils.filter(this.getInMemoryEntityList(), new Predicate<BackupInfoModel>() {
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
