package com.slamcode.goalcalendar.view.charts;

import android.databinding.BindingAdapter;
import android.databinding.ObservableList;

import com.slamcode.goalcalendar.dagger2.Dagger2ComponentContainer;
import com.slamcode.goalcalendar.view.charts.data.ChartDataViewTransformer;
import com.slamcode.goalcalendar.view.charts.data.hellocharts.HelloChartsViewDataTransformer;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

import javax.inject.Inject;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by schlaffeck on 11.04.2017.
 */

public class ChartsBindings {

    @BindingAdapter("bind:pieChartCategoryPlansSource")
    public static void setCategoryPlansSource(PieChartView pieChartView, ObservableList<CategoryPlansViewModel> itemsSource)
    {
        PieChartData data = providePieChartData(itemsSource);
        pieChartView.setPieChartData(data);
        pieChartView.invalidate();
    }

    private static PieChartData providePieChartData(ObservableList<CategoryPlansViewModel> itemsSource) {

        ChartsBindings.Dagger2InjectData injectData = new ChartsBindings.Dagger2InjectData();
        Dagger2ComponentContainer.getApplicationDagger2Component().inject(injectData);
        return injectData.helloChartsViewDataTransformer.providePieChartViewData(itemsSource);
    }

    public static class Dagger2InjectData{

        @Inject
        HelloChartsViewDataTransformer helloChartsViewDataTransformer;
    }
}
