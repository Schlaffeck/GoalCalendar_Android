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
        private String message;
        private String sourceName;
        private boolean enabled;

        public SourceDisplayData(String sourceName)
        {
            this(sourceName, true, "");
        }

        public SourceDisplayData(String sourceName, boolean enabled, String message)
        {
            this.sourceName = sourceName;
            this.enabled = enabled;
            this.message = message;
        }

        public String getSourceName() {
            return sourceName;
        }

        public void setSourceName(String sourceName) {
            this.sourceName = sourceName;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
