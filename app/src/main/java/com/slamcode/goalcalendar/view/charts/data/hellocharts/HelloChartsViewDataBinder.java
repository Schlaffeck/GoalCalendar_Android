package com.slamcode.goalcalendar.view.charts.data.hellocharts;

import android.databinding.Observable;
import android.databinding.ObservableList;

import com.android.databinding.library.baseAdapters.BR;
import com.slamcode.collections.ElementSelector;
import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.view.charts.data.ChartViewDataBinder;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by schlaffeck on 18.04.2017.
 */

public class HelloChartsViewDataBinder implements ChartViewDataBinder<PieChartView> {

    private final ApplicationContext applicationContext;

    public HelloChartsViewDataBinder(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
        this.categoryCallbacksMap = new HashMap<>();
    }

    private Map<CategoryPlansViewModel, CategoryPropertyChangedCallback> categoryCallbacksMap;

    @Override
    public void setupPieChartViewData(PieChartView pieChartView, Iterable<CategoryPlansViewModel> categories) {
        this.setupPieChartViewDataInternal(pieChartView, categories);
    }

    private void setupPieChartViewDataInternal(PieChartView pieChartView, Iterable<CategoryPlansViewModel> itemsSource) {

        List<SliceValue> slices = new ArrayList<>();

        int summarizedNoOfTasks = this.countAllExpectedTasks(itemsSource);

        for(CategoryPlansViewModel category : itemsSource)
        {
            if(!this.categoryCallbacksMap.containsKey(category)) {

                CategoryPropertyChangedCallback categoryChangedCallback =
                        new CategoryPropertyChangedCallback(
                                pieChartView,
                                itemsSource,
                                new SliceValue(0f),
                                new SliceValue(0f),
                                new SliceValue(0f));
                category.addOnPropertyChangedCallback(categoryChangedCallback);
                this.categoryCallbacksMap.put(category, categoryChangedCallback);
            }

            SliceValue doneSliceValue = this.categoryCallbacksMap.get(category).doneSliceValue;
            SliceValue toDoSliceValue = this.categoryCallbacksMap.get(category).todoSliceValue;
            SliceValue exceededSliceValue = this.categoryCallbacksMap.get(category).exceededSliceValue;
            this.setupSlicesValues(summarizedNoOfTasks, category, doneSliceValue, toDoSliceValue, exceededSliceValue, false);
            slices.add(doneSliceValue);
            slices.add(toDoSliceValue);
            slices.add(exceededSliceValue);
        }

        PieChartData data = new PieChartData(slices);
        data.setHasLabelsOnlyForSelected(true);
        pieChartView.setPieChartData(data);
    }

    private int countAllExpectedTasks(Iterable<CategoryPlansViewModel> itemsSource) {
        return com.slamcode.collections.CollectionUtils.sum(itemsSource, new ElementSelector<CategoryPlansViewModel, Integer>() {

            @Override
            public Integer select(CategoryPlansViewModel parent) {
                return parent.getNoOfExpectedTasks();
            }
        });
    }

    private void setupSlicesValues(int summarizedNoOfTasks,
                                   CategoryPlansViewModel category,
                                   SliceValue doneSliceValue,
                                   SliceValue toDoSliceValue,
                                   SliceValue exceededSliceValue,
                                   boolean updateOnly)
    {

        int expectedTasks = category.getNoOfExpectedTasks();
        int successfulTasks = category.getNoOfSuccessfulTasks();

        float doneValue = (successfulTasks <= expectedTasks ? successfulTasks : expectedTasks) * 1.0f / summarizedNoOfTasks;
        float todoValue = successfulTasks >= expectedTasks ? 0 : (expectedTasks - successfulTasks) * 1.0f / summarizedNoOfTasks;
        float exceededValue = successfulTasks <= expectedTasks ? 0.0f : (successfulTasks - expectedTasks) * 1.0f / summarizedNoOfTasks;

        int lightColor = ChartUtils.nextColor();
        int color = ChartUtils.darkenColor(lightColor);
        if(updateOnly)
        {
            doneSliceValue.setTarget(doneValue);
        }
        else {
            doneSliceValue.setValue(doneValue);
            doneSliceValue.setColor(color);
        }
        doneSliceValue.setLabel(String.format("%s, DONE: %d tasks", category.getName(), (successfulTasks <= expectedTasks ? successfulTasks : expectedTasks)));

            if(updateOnly)
            {
                toDoSliceValue.setTarget(todoValue);
            }
            else {
                toDoSliceValue.setValue(todoValue);
                toDoSliceValue.setColor(lightColor);
            }
        toDoSliceValue.setLabel(String.format("%s, TODO: %d tasks", category.getName(), expectedTasks - successfulTasks));

            if(updateOnly)
            {
                exceededSliceValue.setTarget(exceededValue);
            }
            else {
                exceededSliceValue.setValue(exceededValue);
                exceededSliceValue.setColor(ChartUtils.darkenColor(color));
            }
            exceededSliceValue.setLabel(String.format("%s, OVERDONE: %d tasks", category.getName(), successfulTasks - expectedTasks));
    }

    class CategoryPropertyChangedCallback extends Observable.OnPropertyChangedCallback{

        private final PieChartView pieChartView;
        private final Iterable<CategoryPlansViewModel> categories;
        private final SliceValue todoSliceValue;
        private final SliceValue doneSliceValue;
        private final SliceValue exceededSliceValue;

        CategoryPropertyChangedCallback(PieChartView pieChartView,
                                        Iterable<CategoryPlansViewModel> categories,
                                        SliceValue todoSliceValue,
                                        SliceValue doneSliceValue,
                                        SliceValue exceededSliceValue)
        {
            this.pieChartView = pieChartView;
            this.categories = categories;
            this.todoSliceValue = todoSliceValue;
            this.doneSliceValue = doneSliceValue;
            this.exceededSliceValue = exceededSliceValue;
        }

        @Override
        public void onPropertyChanged(Observable observable, int propertyId) {

            if(propertyId == BR.progressPercentage)
            {
                setupSlicesValues(countAllExpectedTasks(this.categories), (CategoryPlansViewModel)observable, this.doneSliceValue, this.todoSliceValue, this.exceededSliceValue, true);
                this.pieChartView.startDataAnimation();
            }
        }
    }
}
