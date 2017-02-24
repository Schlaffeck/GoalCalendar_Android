package com.slamcode.goalcalendar.view.presenters;

import android.app.AlertDialog;

import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.view.AddEditCategoryDialog;
import com.slamcode.goalcalendar.view.CategoryDailyPlansRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.CategoryNameRecyclerViewAdapter;

/**
 * Created by moriasla on 09.02.2017.
 */
public interface MonthlyGoalsPresenter {
    void setYearAndMonth(int year, Month month);

    CategoryNameRecyclerViewAdapter getCategoryNamesRecyclerViewAdapter();

    CategoryDailyPlansRecyclerViewAdapter getCategoryDailyPlansRecyclerViewAdapter();

    boolean isEmptyCategoriesList();

    boolean canCopyCategoriesFromPreviousMonth();

    int getSelectedYear();

    Month getSelectedMonth();

    String getCategoryNameOnPosition(int categoryPosition);

    void copyCategoriesFromPreviousMonth();

    AddEditCategoryDialog createAddEditCategoryDialog(int categoryPosition);

    AlertDialog createDeleteCategoryDialog(int categoryPosition);

    double getProgressSummaryValue();
}
