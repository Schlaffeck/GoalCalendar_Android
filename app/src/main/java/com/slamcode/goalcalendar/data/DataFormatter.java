package com.slamcode.goalcalendar.data;

import com.slamcode.goalcalendar.data.model.backup.BackupDataBundle;
import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;

public interface DataFormatter {

    String formatDataBundle(MonthlyPlansDataBundle dataBundle);

    String formatDataBundle(BackupDataBundle dataBundle);
}
