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
import lecho.lib.hellocharts.renderer.ChartRenderer;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by schlaffeck on 18.04.2017.
 */

public class HelloChartsViewDataBinder implements ChartViewDataBinder<PieChartViewWithProgress> {

    private final ApplicationContext applicationContext;
    private int lastColor = ChartUtils.DEFAULT_COLOR;

    private Map<CategoryPlansViewModel, CategoryPropertyChangedCallback> categoryCallbacksMap;

    public HelloChartsViewDataBinder(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
        this.categoryCallbacksMap = new HashMap<>();
    }

    @Override
    public void setupCategoriesSummaryPieChartViewData(PieChartViewWithProgress pieChartView, Iterable<CategoryPlansViewModel> categories) {
        this.setupPieChartViewDataInternal(pieChartView, categories);
    }

    private void setupPieChartViewDataInternal(PieChartViewWithProgress pieChartView, Iterable<CategoryPlansViewModel> itemsSource) {

        if(!itemsSource.iterator().hasNext())
        {
            pieChartView.setVisibility(View.GONE);
        }
        else {
            pieChartView.setVisibility(View.VISIBLE);
            List<SliceValue> slices = new ArrayList<>();

            int summarizedNoOfTasks = this.countAllExpectedTasks(itemsSource);

            for (CategoryPlansViewModel category : itemsSource) {
                if (!this.categoryCallbacksMap.containsKey(category)) {

                    ProgressSliceValue sliceValue = new ProgressSliceValue();
                    sliceValue.setColor(this.getNextColor());
                    CategoryPropertyChangedCallback categoryChangedCallback =
                            new CategoryPropertyChangedCallback(
                                    pieChartView,
                                    itemsSource,
                                    sliceValue);
                    category.addOnPropertyChangedCallback(categoryChangedCallback);
                    this.categoryCallbacksMap.put(category, categoryChangedCallback);
                }

                ProgressSliceValue sliceValue = this.categoryCallbacksMap.get(category).sliceValue;
                this.setupSlicesValues(summarizedNoOfTasks, category, sliceValue, false);
                slices.add(sliceValue);
            }

            PieChartData data = new PieChartData(slices);
            data.setHasLabelsOnlyForSelected(true);
            data.setSlicesSpacing(0);
            pieChartView.setChartRotationEnabled(false);
            pieChartView.setSelectedValueToggleEnabled(true);
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

    private void setupSlicesValues(int summarizedNoOfTasks,
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
        }
        else
        {
            sliceValue.setValue(targetValue);
        }
        sliceValue.setLabel(
                String.format(
                        this.applicationContext.getStringFromResources(R.string.monthly_plans_summary_generalPieChart_simple_sliceLabel),
                        category.getName(),
                        category.getNoOfSuccessfulTasks(),
                        category.getNoOfExpectedTasks()));
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
        private final ProgressSliceValue sliceValue;

        CategoryPropertyChangedCallback(PieChartView pieChartView,
                                        Iterable<CategoryPlansViewModel> categories,
                                        ProgressSliceValue sliceValue)
        {
            this.pieChartView = pieChartView;
            this.categories = categories;
            this.sliceValue = sliceValue;
        }

        @Override
        public void onPropertyChanged(Observable observable, int propertyId) {

            if(propertyId == BR.progressPercentage)
            {
                int expectedTasksCount = countAllExpectedTasks(this.categories);
                for(Map.Entry<CategoryPlansViewModel, CategoryPropertyChangedCallback> item : categoryCallbacksMap.entrySet()) {
                    setupSlicesValues(expectedTasksCount, item.getKey(), item.getValue().sliceValue, true);
                }
                this.pieChartView.startDataAnimation();
            }
        }
    }
}
