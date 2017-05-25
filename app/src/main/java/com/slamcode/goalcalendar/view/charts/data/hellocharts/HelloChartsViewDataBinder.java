package com.slamcode.goalcalendar.view.charts.data.hellocharts;

import android.databinding.Observable;

import com.android.databinding.library.baseAdapters.BR;
import com.slamcode.collections.ElementSelector;
import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.charts.data.ChartViewDataBinder;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.provider.PieChartDataProvider;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by schlaffeck on 18.04.2017.
 */

public class HelloChartsViewDataBinder implements ChartViewDataBinder<PieChartView> {

    private final static float SLICE_MINIMAL_VALUE = 0.0f;

    private final ApplicationContext applicationContext;
    private int lastColor = ChartUtils.DEFAULT_COLOR;

    private Map<CategoryPlansViewModel, CategoryPropertyChangedCallback> categoryCallbacksMap;

    public HelloChartsViewDataBinder(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
        this.categoryCallbacksMap = new HashMap<>();
    }

    @Override
    public void setupCategoriesSummaryPieChartViewData(PieChartView pieChartView, Iterable<CategoryPlansViewModel> categories) {
        this.setupPieChartViewDataInternal(pieChartView, categories);
    }

    private void setupPieChartViewDataInternal(PieChartView pieChartView, Iterable<CategoryPlansViewModel> itemsSource) {

        List<SliceValue> slices = new ArrayList<>();

        int summarizedNoOfTasks = this.countAllExpectedTasks(itemsSource);

        if(summarizedNoOfTasks == 0)
            pieChartView.setVisibility(View.GONE);
        else {

            pieChartView.setVisibility(View.VISIBLE);
                CategoryPropertyChangedCallback categoryChangedCallback =
                        new CategoryPropertyChangedCallback(
                                pieChartView,
                                itemsSource,
                                new ProgressSliceValue());
                category.addOnPropertyChangedCallback(categoryChangedCallback);
                this.categoryCallbacksMap.put(category, categoryChangedCallback);

            for (CategoryPlansViewModel category : itemsSource) {
                if (!this.categoryCallbacksMap.containsKey(category)) {

                    int lightColor = this.getNextColor();
                    int color =  ChartUtils.darkenColor(lightColor);
                    int darkenColor = ChartUtils.darkenColor(color);

                    CategoryPropertyChangedCallback categoryChangedCallback =
                            new CategoryPropertyChangedCallback(
                                    pieChartView,
                                    itemsSource,
                                    slices,
                                    new SliceValue(0f, color),
                                    new SliceValue(0f, lightColor),
                                    new SliceValue(0f, darkenColor));
                    category.addOnPropertyChangedCallback(categoryChangedCallback);
                    this.categoryCallbacksMap.put(category, categoryChangedCallback);
                }

                SliceValue doneSliceValue = this.categoryCallbacksMap.get(category).doneSliceValue;
                SliceValue toDoSliceValue = this.categoryCallbacksMap.get(category).todoSliceValue;
                SliceValue exceededSliceValue = this.categoryCallbacksMap.get(category).exceededSliceValue;
                this.setupSlicesValues(slices, summarizedNoOfTasks, category, doneSliceValue, toDoSliceValue, exceededSliceValue, false);
                if(doneSliceValue.getValue() > 0)
                    slices.add(doneSliceValue);

                if(toDoSliceValue.getValue() > 0)
                    slices.add(toDoSliceValue);

                if(exceededSliceValue.getValue() > 0)
                    slices.add(exceededSliceValue);
            }

            ProgressSliceValue sliceValue = this.categoryCallbacksMap.get(category).todoSliceValue;
            this.setupSlicesValues(summarizedNoOfTasks, category, sliceValue, false);
            slices.add(sliceValue);
            PieChartData data = new PieChartData(slices);
            data.setHasLabelsOnlyForSelected(true);
            data.setSlicesSpacing(0);

            pieChartView.setChartRotationEnabled(false);
            pieChartView.setPieChartData(data);
        }

        PieChartData data = new PieChartData(slices);
        data.setHasLabelsOnlyForSelected(true);
        pieChartView.setChartRenderer(new ProgressPieChartRenderer(this.applicationContext.getDefaultContext(), pieChartView, pieChartView));
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

    private void setupSlicesValues(List<SliceValue> slices, int summarizedNoOfTasks,
                                   CategoryPlansViewModel category,
                                   ProgressSliceValue sliceValue,
                                   boolean updateOnly)
    {
        sliceValue.setProgressValue(category.getNoOfSuccessfulTasks());
        sliceValue.setThresholdValue(category.getNoOfExpectedTasks());

        float targetValue = category.getNoOfExpectedTasks() * 1.0f / summarizedNoOfTasks;

        if(updateOnly)
        {
            sliceValue.setTarget(targetValue);
            if(!slices.contains(doneSliceValue) && doneValue > 0)
            {
                // add missing slice to list
                slices.add(slices.indexOf(toDoSliceValue), doneSliceValue);
            }
            else if(doneValue == 0 && slices.contains(doneSliceValue))
                slices.remove(doneSliceValue);
        }
        else
        {
            sliceValue.setLabel(
                    String.format(
                            this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_generalPieChart_simple_sliceLabel),
                            category.getName(),
                            category.getNoOfSuccessfulTasks(),
                            category.getNoOfExpectedTasks()));
            sliceValue.setValue(targetValue);
        }
    }

    private int getNextColor()
    {
        int color = this.lastColor;
        if(this.lastColor == ChartUtils.COLOR_BLUE)
                color = ChartUtils.COLOR_GREEN;

        else if(this.lastColor == ChartUtils.COLOR_GREEN)
            color = ChartUtils.COLOR_ORANGE;

        else if(this.lastColor == ChartUtils.COLOR_ORANGE)
            color = ChartUtils.COLOR_RED;

        else if(this.lastColor == ChartUtils.COLOR_RED)
            color = ChartUtils.COLOR_VIOLET;

        else color = ChartUtils.COLOR_BLUE;

        return this.lastColor = color;
    }

    class CategoryPropertyChangedCallback extends Observable.OnPropertyChangedCallback{

        private final PieChartView pieChartView;
        private final Iterable<CategoryPlansViewModel> categories;
        private final ProgressSliceValue todoSliceValue;

        CategoryPropertyChangedCallback(PieChartView pieChartView,
                                        Iterable<CategoryPlansViewModel> categories,
                                        ProgressSliceValue todoSliceValue)
        {
            this.pieChartView = pieChartView;
            this.categories = categories;
            this.slices = slices;
            this.todoSliceValue = todoSliceValue;
        }

        @Override
        public void onPropertyChanged(Observable observable, int propertyId) {

            if(propertyId == BR.progressPercentage)
            {
                int expectedTasksCount = countAllExpectedTasks(this.categories);
                for(Map.Entry<CategoryPlansViewModel, CategoryPropertyChangedCallback> item : categoryCallbacksMap.entrySet()) {
                    setupSlicesValues(expectedTasksCount, item.getKey(), item.getValue().todoSliceValue, true);
                }
                this.pieChartView.startDataAnimation();
            }
        }
    }
}
