package com.slamcode.goalcalendar.view.presenters;

import com.slamcode.goalcalendar.data.model.CategoryModel;

/**
 * Created by moriasla on 09.02.2017.
 */
public interface DayPlanSummaryPresenter {
    Iterable<CategoryModel> getPlannedCategories();

    Iterable<CategoryModel> getSuccessfulCategories();
}
