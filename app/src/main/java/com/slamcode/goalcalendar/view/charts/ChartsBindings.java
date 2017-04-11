package com.slamcode.goalcalendar.view.charts;

import android.content.res.Resources;
import android.databinding.BindingAdapter;
import android.databinding.ObservableList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.google.common.collect.Collections2;
import com.slamcode.collections.ElementSelector;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
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
    }

    private static PieChartData providePieChartData(ObservableList<CategoryPlansViewModel> itemsSource) {

        List<SliceValue> slices = new ArrayList<>();

        int summarizedNoOfTasks = com.slamcode.collections.CollectionUtils.sum(itemsSource, new ElementSelector<CategoryPlansViewModel, Integer>() {

            @Override
            public Integer select(CategoryPlansViewModel parent) {
                return parent.getNoOfExpectedTasks();
            }
        });

        for(CategoryPlansViewModel category : itemsSource)
        {
            int expectedTasks = category.getNoOfExpectedTasks();
            int successfulTasks = category.getNoOfSuccessfulTasks();
            int lightColor = ChartUtils.nextColor();
            int color = ChartUtils.darkenColor(lightColor);

            SliceValue slice = new SliceValue();
            slice.setValue((successfulTasks <= expectedTasks ? successfulTasks : expectedTasks) * 1.0f / summarizedNoOfTasks);
            slice.setColor(color);
            slice.setLabel(String.format("%s, DONE: %d tasks", category.getName(), (successfulTasks <= expectedTasks ? successfulTasks : expectedTasks)));
            slices.add(slice);

            if(successfulTasks < expectedTasks) {
                SliceValue toDoSliceValue = new SliceValue();
                toDoSliceValue.setValue((expectedTasks - successfulTasks) * 1.0f / summarizedNoOfTasks);
                toDoSliceValue.setColor(lightColor);
                toDoSliceValue.setLabel(String.format("%s, TODO: %d tasks", category.getName(), expectedTasks - successfulTasks));
                slices.add(toDoSliceValue);

            }
            else if(successfulTasks > expectedTasks)
            {
                SliceValue exceedSlice = new SliceValue();
                exceedSlice.setValue((successfulTasks - expectedTasks) * 1.0f / summarizedNoOfTasks);
                exceedSlice.setColor(ChartUtils.darkenColor(color));
                exceedSlice.setLabel(String.format("%s, OVERDONE: %d tasks", category.getName(), successfulTasks - expectedTasks));
                slices.add(exceedSlice);
            }
        }

        PieChartData data = new PieChartData(slices);
        data.setHasLabelsOnlyForSelected(true);
        return data;
    }
}
