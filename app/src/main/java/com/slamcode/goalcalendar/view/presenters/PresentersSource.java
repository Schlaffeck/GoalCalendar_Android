package com.slamcode.goalcalendar.view.presenters;

import com.slamcode.goalcalendar.MonthlyGoalsActivity;
import com.slamcode.goalcalendar.viewmodels.MonthlyGoalsViewModel;

/**
 * Created by moriasla on 24.02.2017.
 */

public interface PresentersSource {

    MonthlyGoalsPresenter getMonthlyGoalsPresenter(MonthlyGoalsActivity activity);
}
