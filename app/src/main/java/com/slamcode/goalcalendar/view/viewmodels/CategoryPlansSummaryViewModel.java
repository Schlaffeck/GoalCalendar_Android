package com.slamcode.goalcalendar.view.viewmodels;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;

import java.text.Collator;
import java.util.Locale;

/**
 * Created by moriasla on 28.02.2017.
 */

public class CategoryPlansSummaryViewModel extends BaseObservable implements Comparable<CategoryPlansSummaryViewModel> {

    private final PlansSummaryCalculator.CategoryPlansSummary model;

    public CategoryPlansSummaryViewModel(PlansSummaryCalculator.CategoryPlansSummary model)
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

    @Override
    public int compareTo(CategoryPlansSummaryViewModel o) {
        if(o == null)
            return 1;

        Collator usCollator = Collator.getInstance(Locale.getDefault());
        usCollator.setStrength(Collator.PRIMARY);
        return usCollator.compare(this.getName(), o.getName());
    }
}
