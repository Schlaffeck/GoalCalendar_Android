package com.slamcode.goalcalendar.view.activity;

import android.app.DialogFragment;
import android.view.View;

import com.slamcode.goalcalendar.viewmodels.MonthlyGoalsViewModel;

/**
 * Contract interface between monthly goals activity view and its presenter
 */

public interface MonthlyGoalsActivityContract {

    /**
     * Presenter contains actions for view elements, that can modify the data
     */
    interface Presenter {

        MonthlyGoalsViewModel getData();

        void setData(MonthlyGoalsViewModel data);

        void initializeWithView(ActivityView view);

        void copyCategoriesFromPreviousMonth(View view);

        void goToNextYear(View view);

        void goToPreviousYear(View view);

        void goToNextMonth(View view);

        void goToPreviousMonth(View view);

        void showAddNewCategoryDialog(View view);
    }

    /**
     * View has simple logic of managing binding presenters actions and data to concrete controls
     */
    interface ActivityView{

        void onDataSet(MonthlyGoalsViewModel data);

        void showDialog(DialogFragment dialogFragment);
    }
}
