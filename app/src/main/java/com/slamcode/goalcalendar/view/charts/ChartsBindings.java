package com.slamcode.goalcalendar.view.charts;

import android.databinding.BindingAdapter;
import android.databinding.ObservableList;

import com.slamcode.goalcalendar.dagger2.Dagger2ComponentContainer;
import com.slamcode.goalcalendar.view.charts.data.hellocharts.HelloChartsViewDataBinder;
import com.slamcode.goalcalendar.view.charts.data.hellocharts.PieChartViewWithProgress;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

import javax.inject.Inject;

import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by schlaffeck on 11.04.2017.
 */

public class ChartsBindings {

    @BindingAdapter("bind:pieChartCategoryPlansSource")
    public static void setCategoryPlansSource(PieChartViewWithProgress pieChartView, ObservableList<CategoryPlansViewModel> itemsSource)
    {
        ChartsBindings.Dagger2InjectData injectData = new ChartsBindings.Dagger2InjectData();
        Dagger2ComponentContainer.getApplicationDagger2Component().inject(injectData);
        injectData.helloChartsViewDataTransformer.setupCategoriesSummaryPieChartViewData(pieChartView, itemsSource);
    }

    public static class Dagger2InjectData{

        @Inject
        HelloChartsViewDataBinder helloChartsViewDataTransformer;
    }
}
