package com.slamcode.goalcalendar.view.dialogs;

import android.view.LayoutInflater;
import android.view.View;

import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;
import com.slamcode.goalcalendar.viewmodels.MonthViewModel;

/**
 * Created by moriasla on 06.03.2017.
 */

public class AddEditCategoryViewModelDialog extends AddEditDialog<CategoryPlansViewModel> {

    private MonthViewModel monthData;

    @Override
    protected View initializeView(LayoutInflater inflater) {
        return null;
    }

    @Override
    protected void commitChanges() {

    }

    public MonthViewModel getMonthData() {
        return monthData;
    }

    public void setMonthData(MonthViewModel monthData) {
        this.monthData = monthData;
    }
}
