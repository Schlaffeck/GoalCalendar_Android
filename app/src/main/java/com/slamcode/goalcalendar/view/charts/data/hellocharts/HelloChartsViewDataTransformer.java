package com.slamcode.goalcalendar.view.charts.data.hellocharts;

import com.slamcode.collections.ElementSelector;
import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.view.charts.data.ChartDataViewTransformer;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;

/**
 * Created by schlaffeck on 18.04.2017.
 */

public class HelloChartsViewDataTransformer implements ChartDataViewTransformer<PieChartData> {

    private final ApplicationContext applicationContext;

    public HelloChartsViewDataTransformer(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    @Override
    public PieChartData providePieChartViewData(Iterable<CategoryPlansViewModel> itemsSource) {

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
