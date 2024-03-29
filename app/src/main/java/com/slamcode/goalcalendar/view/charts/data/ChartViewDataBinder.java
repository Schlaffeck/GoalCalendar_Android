package com.slamcode.goalcalendar.view.charts.data;

import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

/**
 * Created by schlaffeck on 18.04.2017.
 */

public interface ChartViewDataBinder<PieChartViewType> {

    void setupCategoriesSummaryPieChartViewData(PieChartViewType pieChartView, Iterable<CategoryPlansViewModel> categories);
}
