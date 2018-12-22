package com.slamcode.goalcalendar.backup;

import java.util.Locale;

public interface BackupSourceDataProvider {

    String getSourceType();

    String getUserSourceIdentifier();

    SourceDisplayData getDisplayData(Locale locale);

    BackupWriter getBackupWriter();

    BackupRestorer getBackupRestorer();

    class SourceDisplayData
    {
        private String sourceName;

        public SourceDisplayData(String sourceName)
        {
            this.sourceName = sourceName;
        }

        public String getSourceName() {
            return sourceName;
        }

        public void setSourceName(String sourceName) {
            this.sourceName = sourceName;
        }
    }
}
