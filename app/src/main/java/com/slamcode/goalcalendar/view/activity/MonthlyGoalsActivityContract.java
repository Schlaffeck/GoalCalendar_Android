package com.slamcode.goalcalendar.view.activity;

import android.app.DialogFragment;
import android.view.View;

import com.slamcode.goalcalendar.view.OnLayoutReadyCallback;
import com.slamcode.goalcalendar.view.activity.base.ActivityViewContract;
import com.slamcode.goalcalendar.view.activity.base.PresenterContract;
import com.slamcode.goalcalendar.viewmodels.MonthlyGoalsViewModel;

/**
 * Contract interface between monthly goals activity view and its presenter
 */

public interface MonthlyGoalsActivityContract {

    /**
     * Presenter contains actions for view elements, that can modify the data
     */
    interface Presenter extends PresenterContract<MonthlyGoalsViewModel, ActivityView> {

        MonthlyGoalsViewModel getData();

        void copyCategoriesFromPreviousMonth(View view);

        void goToNextYear(View view);

        void goToPreviousYear(View view);

        void goToNextMonth(View view);

        void goToPreviousMonth(View view);

        void showAddNewCategoryDialog(View view);

        OnLayoutReadyCallback getOnCategoryListReadyCallback();
    }

    /**
     * View has simple logic of managing binding presenters actions and data to concrete controls
     */
    interface ActivityView extends ActivityViewContract<MonthlyGoalsViewModel>
    {
        void showDialog(DialogFragment dialogFragment);

        void scrollToCurrentDate();
    }
}
