package com.slamcode.goalcalendar.view.charts.data.hellocharts;

import android.databinding.Observable;
import android.view.View;

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
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by schlaffeck on 18.04.2017.
 */

public class HelloChartsViewDataBinder implements ChartViewDataBinder<PieChartView> {

    private final static float SLICE_MINIMAL_VALUE = 0.0f;

    private final ApplicationContext applicationContext;
    private int lastColor = ChartUtils.DEFAULT_COLOR;

    public HelloChartsViewDataBinder(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
        this.categoryCallbacksMap = new HashMap<>();
    }

    private Map<CategoryPlansViewModel, CategoryPropertyChangedCallback> categoryCallbacksMap;

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

            PieChartData data = new PieChartData(slices);
            data.setHasLabelsOnlyForSelected(true);
            data.setSlicesSpacing(0);

            pieChartView.setChartRotationEnabled(false);
            pieChartView.setPieChartData(data);
        }
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
                                   SliceValue doneSliceValue,
                                   SliceValue toDoSliceValue,
                                   SliceValue exceededSliceValue,
                                   boolean updateOnly)
    {

        int expectedTasks = category.getNoOfExpectedTasks();
        int successfulTasks = category.getNoOfSuccessfulTasks();

        float doneValue = (successfulTasks <= expectedTasks ? successfulTasks : expectedTasks) * 1.0f / summarizedNoOfTasks;
        float todoValue = successfulTasks >= expectedTasks ? SLICE_MINIMAL_VALUE : (expectedTasks - successfulTasks) * 1.0f / summarizedNoOfTasks;
        float exceededValue = successfulTasks <= expectedTasks ? SLICE_MINIMAL_VALUE : (successfulTasks - expectedTasks) * 1.0f / summarizedNoOfTasks;

        if(updateOnly)
        {
            doneSliceValue.setTarget(doneValue);
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
            doneSliceValue.setValue(doneValue);
        }
        doneSliceValue.setLabel(String.format(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_generalPieChart_done_sliceLabel), category.getName(), (successfulTasks <= expectedTasks ? successfulTasks : expectedTasks)));

            if(updateOnly)
            {
                toDoSliceValue.setTarget(todoValue);

                if(!slices.contains(toDoSliceValue) && todoValue > 0)
                {
                    // add missing slice to list
                    slices.add(slices.indexOf(doneSliceValue) +1, toDoSliceValue);
                }
                else if(todoValue == 0 && slices.contains(toDoSliceValue))
                    slices.remove(toDoSliceValue);
            }
            else {
                toDoSliceValue.setValue(todoValue);
            }
        toDoSliceValue.setLabel(String.format(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_generalPieChart_todo_sliceLabel), category.getName(), expectedTasks - successfulTasks));

            if(updateOnly)
            {
                exceededSliceValue.setTarget(exceededValue);

                if(!slices.contains(exceededSliceValue) && exceededValue > 0)
                {
                    // add missing slice to list
                    slices.add(slices.indexOf(doneSliceValue) +1, exceededSliceValue);
                }
                else if(exceededValue == 0 && slices.contains(exceededSliceValue))
                    slices.remove(exceededSliceValue);
            }
            else {
                exceededSliceValue.setValue(exceededValue);
            }
            exceededSliceValue.setLabel(String.format(this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_generalPieChart_overdone_sliceLabel), category.getName(), successfulTasks - expectedTasks));
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
        private final List<SliceValue> slices;
        private final SliceValue todoSliceValue;
        private final SliceValue doneSliceValue;
        private final SliceValue exceededSliceValue;

        CategoryPropertyChangedCallback(PieChartView pieChartView,
                                        Iterable<CategoryPlansViewModel> categories,
                                        List<SliceValue> slices,
                                        SliceValue todoSliceValue,
                                        SliceValue doneSliceValue,
                                        SliceValue exceededSliceValue)
        {
            this.pieChartView = pieChartView;
            this.categories = categories;
            this.slices = slices;
            this.todoSliceValue = todoSliceValue;
            this.doneSliceValue = doneSliceValue;
            this.exceededSliceValue = exceededSliceValue;
        }

        @Override
        public void onPropertyChanged(Observable observable, int propertyId) {

            if(propertyId == BR.progressPercentage)
            {
                setupSlicesValues(this.slices, countAllExpectedTasks(this.categories), (CategoryPlansViewModel)observable, this.doneSliceValue, this.todoSliceValue, this.exceededSliceValue, true);
                this.pieChartView.startDataAnimation();
            }
        }
    }
}
