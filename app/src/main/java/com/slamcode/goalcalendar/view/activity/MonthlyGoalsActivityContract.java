package com.slamcode.goalcalendar.view.activity;

import android.app.DialogFragment;
import android.view.View;

import com.slamcode.goalcalendar.view.DataBasedPresenter;
import com.slamcode.goalcalendar.view.DataBasedView;
import com.slamcode.goalcalendar.viewmodels.MonthlyGoalsViewModel;

/**
 * Created by moriasla on 06.03.2017.
 */

public interface MonthlyGoalsActivityContract {

    /**
     * Presenter contains actions for view elements, that can modify the data
     */
    interface Presenter extends DataBasedPresenter<MonthlyGoalsViewModel> {

        boolean isPreviousMonthWithCategoriesAvailable();

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
    interface ActivityView extends DataBasedView<MonthlyGoalsViewModel>{

        void showDialog(DialogFragment dialogFragment);
    }
}
