package com.slamcode.goalcalendar.view.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;

import java.text.Collator;
import java.util.Locale;

/**
 * Created by moriasla on 28.02.2017.
 */

public class PlansSummaryForCategoryViewModel extends BaseObservable implements Comparable<PlansSummaryForCategoryViewModel> {

    private final PlansSummaryCalculator.CategoryPlansSummary model;

    public PlansSummaryForCategoryViewModel(PlansSummaryCalculator.CategoryPlansSummary model)
    {
        this.model = model;
    }

    @Bindable
    public String getName()
    {
        return this.model.getCategoryName();
    }

    @Bindable
    public boolean isDataAvailable()
    {
        return this.model.getDataAvailable();
    }

    @Bindable
    public double getProgressPercentage()
    {
        return this.model.getProgressPercentage();
    }

    @Bindable
    public int getNoOfExpectedTasks()
    {
        return this.model.getNoOfExpectedTasks();
    }

    @Bindable
    public int getNoOfFailedTasks()
    {
        return this.model.getNoOfFailedTasks();
    }

    @Bindable
    public int getNoOfSuccessfulTasks()
    {
        return this.model.getNoOfSuccessfulTasks();
    }

    @Override
    public int compareTo(PlansSummaryForCategoryViewModel o) {
        if(o == null)
            return 1;

        Collator usCollator = Collator.getInstance(Locale.getDefault());
        usCollator.setStrength(Collator.PRIMARY);
        return usCollator.compare(this.getName(), o.getName());
    }
}
