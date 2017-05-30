package com.slamcode.goalcalendar.view.charts;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
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

    @BindingAdapter("bind:selectedSliceIndex")
    public static void setSelectedSliceIndex(PieChartViewWithProgress pieChartView, int index)
    {
        if(index == pieChartView.getSelectedIndex())
            return;

        pieChartView.setSelectedIndex(index);
    }

    @InverseBindingAdapter(attribute = "selectedSliceIndex")
    public static int getSelectedSliceIndex(PieChartViewWithProgress pieChartView)
    {
        return pieChartView.getSelectedIndex();
    }

    @BindingAdapter(value = {"bind:selectedSliceIndex", "bind:selectedSliceIndexAttrChanged"}, requireAll = false)
    public static void setSelectedSliceIndexListeners(final PieChartViewWithProgress pieChartView,
                                                      final PieChartViewWithProgress.SelectedIndexChangedListener selectedIndexChangedListener,
                                                      final InverseBindingListener inverseBindingListener)
    {
        if(inverseBindingListener == null)
        {
            pieChartView.addSelectedIndexChangeListener(selectedIndexChangedListener);
        }
        else
        {
            pieChartView.addSelectedIndexChangeListener(new PieChartViewWithProgress.SelectedIndexChangedListener() {
                @Override
                public void indexChanged(int oldIndex, int newIndex) {
                    //pieChartView.removeSelectedIndexChangeListener(this);
                    if(selectedIndexChangedListener != null)
                    {
                        selectedIndexChangedListener.indexChanged(oldIndex, newIndex);
                    }
                    inverseBindingListener.onChange();
                }
            });
        }
    }

    public static class Dagger2InjectData{

        @Inject
        HelloChartsViewDataBinder helloChartsViewDataTransformer;
    }
}
