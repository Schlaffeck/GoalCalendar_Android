package com.slamcode.goalcalendar.view.charts.data.hellocharts;

import android.view.View;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryTestHelper;
import com.slamcode.goalcalendar.viewmodels.CategoryPlansViewModel;
import com.slamcode.goalcalendar.viewmodels.MonthViewModel;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.MockSettings;
import org.mockito.internal.creation.MockSettingsImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.view.PieChartView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by schlaffeck on 23/05/2017.
 */
public class HelloChartsViewDataBinderTest {

    @Test
    public void setupCategoriesSummaryPieChartViewData_emptyData_test()
    {
        // create
        PieChartView pieChartViewMock = mock(PieChartView.class);
        ApplicationContext applicationContextMock = getApplicationContextMock();

        HelloChartsViewDataBinder binder = new HelloChartsViewDataBinder(applicationContextMock);

        // prepare data
        List<CategoryPlansViewModel> modelList = new ArrayList<>();

        // act
        binder.setupCategoriesSummaryPieChartViewData(pieChartViewMock, modelList);

        // capture
        verify(pieChartViewMock, times(0)).setPieChartData(any(PieChartData.class));
        verify(pieChartViewMock).setVisibility(View.GONE);
    }

    @Test
    public void setupCategoriesSummaryPieChartViewData_notStartedCategories_test()
    {
        // mocks
        PieChartView pieChartViewMock = mock(PieChartView.class);
        ApplicationContext applicationContextMock = getApplicationContextMock();
        PlansSummaryCalculator calculatorMock = mock(PlansSummaryCalculator.class);

        HelloChartsViewDataBinder binder = new HelloChartsViewDataBinder(applicationContextMock);

        // prepare data
        PlansSummaryCalculator.CategoryPlansSummary[] plansSummaries = {
                PlansSummaryTestHelper.createCategoryPlansSummary(getCategoryName(1), 2, 0, 0),
                PlansSummaryTestHelper.createCategoryPlansSummary(getCategoryName(2), 2, 0, 0)
        };
        List<CategoryPlansViewModel> modelList = this.createCategoryPlansViewModelList(
                calculatorMock,
                plansSummaries);

        // act
        binder.setupCategoriesSummaryPieChartViewData(pieChartViewMock, modelList);

        // capture
        ArgumentCaptor<PieChartData> dataCaptor = ArgumentCaptor.forClass(PieChartData.class);
        verify(pieChartViewMock).setPieChartData(dataCaptor.capture());
        verify(pieChartViewMock).setVisibility(View.VISIBLE);

        //assert
        PieChartData data = dataCaptor.getValue();
        assertDefaultData(data);
        verify(pieChartViewMock).setChartRenderer(Matchers.notNull(ProgressPieChartRenderer.class));

        // slices
        assertEquals(2, data.getValues().size());

        // cat 1
        assertTrue(data.getValues().get(0) instanceof ProgressSliceValue);
        ProgressSliceValue progressSliceValue = (ProgressSliceValue) data.getValues().get(0);
        assertEquals(0.5f, progressSliceValue.getValue(), 0f);
        assertEquals(2f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(0f, progressSliceValue.getProgressValue(), 0f);
        assertEquals("Category 1, 0/2", new String(progressSliceValue.getLabelAsChars()));

        // cat 2
        assertTrue(data.getValues().get(1) instanceof ProgressSliceValue);
        progressSliceValue = (ProgressSliceValue) data.getValues().get(1);
        assertEquals(0.5f, progressSliceValue.getValue(), 0f);
        assertEquals(2f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(0f, progressSliceValue.getProgressValue(), 0f);
        assertEquals("Category 2, 0/2", new String(progressSliceValue.getLabelAsChars()));
    }


    @Test
    public void setupCategoriesSummaryPieChartViewData_partiallyDoneCategories_test()
    {
        // mocks
        PieChartView pieChartViewMock = mock(PieChartView.class);
        ApplicationContext applicationContextMock = getApplicationContextMock();
        PlansSummaryCalculator calculatorMock = mock(PlansSummaryCalculator.class);

        HelloChartsViewDataBinder binder = new HelloChartsViewDataBinder(applicationContextMock);

        // prepare data
        PlansSummaryCalculator.CategoryPlansSummary[] plansSummaries = {
                PlansSummaryTestHelper.createCategoryPlansSummary(getCategoryName(1), 5, 2, 0),
                PlansSummaryTestHelper.createCategoryPlansSummary(getCategoryName(2), 5, 3, 0)
        };
        List<CategoryPlansViewModel> modelList = this.createCategoryPlansViewModelList(
                calculatorMock,
                plansSummaries);

        // act
        binder.setupCategoriesSummaryPieChartViewData(pieChartViewMock, modelList);

        // capture
        ArgumentCaptor<PieChartData> dataCaptor = ArgumentCaptor.forClass(PieChartData.class);
        verify(pieChartViewMock).setPieChartData(dataCaptor.capture());
        verify(pieChartViewMock).setVisibility(View.VISIBLE);

        //assert
        PieChartData data = dataCaptor.getValue();
        assertDefaultData(data);
        verify(pieChartViewMock).setChartRenderer(Matchers.notNull(ProgressPieChartRenderer.class));

        // slices
        assertEquals(2, data.getValues().size());

        // cat 1
        assertTrue(data.getValues().get(0) instanceof ProgressSliceValue);
        ProgressSliceValue progressSliceValue = (ProgressSliceValue) data.getValues().get(0);
        assertEquals(0.5f, progressSliceValue.getValue(), 0f);
        assertEquals(5f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(2f, progressSliceValue.getProgressValue(), 0f);
        assertEquals("Category 1, 2/5", new String(progressSliceValue.getLabelAsChars()));

        // cat 2
        assertTrue(data.getValues().get(1) instanceof ProgressSliceValue);
        progressSliceValue = (ProgressSliceValue) data.getValues().get(1);
        assertEquals(0.5f, progressSliceValue.getValue(), 0f);
        assertEquals(5f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(3f, progressSliceValue.getProgressValue(), 0f);
        assertEquals("Category 2, 3/5", new String(progressSliceValue.getLabelAsChars()));
    }

    @Test
    public void setupCategoriesSummaryPieChartViewData_doneNotDoneCategories_test()
    {
        // mocks
        PieChartView pieChartViewMock = mock(PieChartView.class);
        ApplicationContext applicationContextMock = getApplicationContextMock();
        PlansSummaryCalculator calculatorMock = mock(PlansSummaryCalculator.class);

        HelloChartsViewDataBinder binder = new HelloChartsViewDataBinder(applicationContextMock);

        // prepare data
        PlansSummaryCalculator.CategoryPlansSummary[] plansSummaries = {
                PlansSummaryTestHelper.createCategoryPlansSummary(getCategoryName(1), 3, 2, 0),
                PlansSummaryTestHelper.createCategoryPlansSummary(getCategoryName(2), 3, 3, 0),
                PlansSummaryTestHelper.createCategoryPlansSummary(getCategoryName(3), 4, 3, 0)
        };
        List<CategoryPlansViewModel> modelList = this.createCategoryPlansViewModelList(
                calculatorMock,
                plansSummaries);

        // act
        binder.setupCategoriesSummaryPieChartViewData(pieChartViewMock, modelList);

        // capture
        ArgumentCaptor<PieChartData> dataCaptor = ArgumentCaptor.forClass(PieChartData.class);
        verify(pieChartViewMock).setPieChartData(dataCaptor.capture());
        verify(pieChartViewMock).setVisibility(View.VISIBLE);

        //assert
        PieChartData data = dataCaptor.getValue();
        assertDefaultData(data);
        verify(pieChartViewMock).setChartRenderer(Matchers.notNull(ProgressPieChartRenderer.class));

        // slices
        assertEquals(3, data.getValues().size());

        // cat 1
        assertTrue(data.getValues().get(0) instanceof ProgressSliceValue);
        ProgressSliceValue progressSliceValue = (ProgressSliceValue) data.getValues().get(0);
        assertEquals(0.3f, progressSliceValue.getValue(), 0f);
        assertEquals(3f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(2f, progressSliceValue.getProgressValue(), 0f);
        assertEquals("Category 1, 2/3", new String(progressSliceValue.getLabelAsChars()));

        // cat 2
        assertTrue(data.getValues().get(1) instanceof ProgressSliceValue);
        progressSliceValue = (ProgressSliceValue) data.getValues().get(1);
        assertEquals(0.3f, progressSliceValue.getValue(), 0f);
        assertEquals(3f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(3f, progressSliceValue.getProgressValue(), 0f);
        assertEquals("Category 2, 3/3", new String(progressSliceValue.getLabelAsChars()));

        // cat 3
        assertTrue(data.getValues().get(2) instanceof ProgressSliceValue);
        progressSliceValue = (ProgressSliceValue) data.getValues().get(2);
        assertEquals(0.4f, progressSliceValue.getValue(), 0f);
        assertEquals(4f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(3f, progressSliceValue.getProgressValue(), 0f);
        assertEquals("Category 3, 3/4", new String(progressSliceValue.getLabelAsChars()));
    }

    @Test
    public void setupCategoriesSummaryPieChartViewData_doneOverdoneCategories_test()
    {
        // mocks
        PieChartView pieChartViewMock = mock(PieChartView.class);
        ApplicationContext applicationContextMock = getApplicationContextMock();
        PlansSummaryCalculator calculatorMock = mock(PlansSummaryCalculator.class);

        HelloChartsViewDataBinder binder = new HelloChartsViewDataBinder(applicationContextMock);

        // prepare data
        PlansSummaryCalculator.CategoryPlansSummary[] plansSummaries = {
                PlansSummaryTestHelper.createCategoryPlansSummary(getCategoryName(1), 2, 3, 0),
                PlansSummaryTestHelper.createCategoryPlansSummary(getCategoryName(2), 3, 3, 0),
                PlansSummaryTestHelper.createCategoryPlansSummary(getCategoryName(3), 5, 3, 0)
        };
        List<CategoryPlansViewModel> modelList = this.createCategoryPlansViewModelList(
                calculatorMock,
                plansSummaries);

        // act
        binder.setupCategoriesSummaryPieChartViewData(pieChartViewMock, modelList);

        // capture
        ArgumentCaptor<PieChartData> dataCaptor = ArgumentCaptor.forClass(PieChartData.class);
        verify(pieChartViewMock).setPieChartData(dataCaptor.capture());
        verify(pieChartViewMock).setVisibility(View.VISIBLE);

        //assert
        PieChartData data = dataCaptor.getValue();
        assertDefaultData(data);
        verify(pieChartViewMock).setChartRenderer(Matchers.notNull(ProgressPieChartRenderer.class));

        // slices
        assertEquals(3, data.getValues().size());

        // cat 1
        assertTrue(data.getValues().get(0) instanceof ProgressSliceValue);
        ProgressSliceValue progressSliceValue = (ProgressSliceValue) data.getValues().get(0);
        assertEquals(0.2f, progressSliceValue.getValue(), 0f);
        assertEquals(2f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(3f, progressSliceValue.getProgressValue(), 0f);
        assertEquals("Category 1, 3/2", new String(progressSliceValue.getLabelAsChars()));

        // cat 2
        assertTrue(data.getValues().get(1) instanceof ProgressSliceValue);
        progressSliceValue = (ProgressSliceValue) data.getValues().get(1);
        assertEquals(0.3f, progressSliceValue.getValue(), 0f);
        assertEquals(3f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(3f, progressSliceValue.getProgressValue(), 0f);
        assertEquals("Category 2, 3/3", new String(progressSliceValue.getLabelAsChars()));

        // cat 3
        assertTrue(data.getValues().get(2) instanceof ProgressSliceValue);
        progressSliceValue = (ProgressSliceValue) data.getValues().get(2);
        assertEquals(0.5f, progressSliceValue.getValue(), 0f);
        assertEquals(5f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(3f, progressSliceValue.getProgressValue(), 0f);
        assertEquals("Category 3, 3/5", new String(progressSliceValue.getLabelAsChars()));
    }

    @Test
    public void setupPieChartViewData_updateSlicesProgressValue_test()
    {
        // mocks
        PieChartView pieChartViewMock = mock(PieChartView.class);
        ApplicationContext applicationContextMock = getApplicationContextMock();
        PlansSummaryCalculator calculatorMock = mock(PlansSummaryCalculator.class);

        HelloChartsViewDataBinder binder = new HelloChartsViewDataBinder(applicationContextMock);

        // prepare data
        PlansSummaryCalculator.CategoryPlansSummary categoryPlansSummary
                = PlansSummaryTestHelper.createCategoryPlansSummary(getCategoryName(1), 10, 0, 0);
        when(calculatorMock.calculatePlansSummaryForMonthInCategory(2017, Month.MAY, "Category 1"))
                .thenReturn(categoryPlansSummary);
        CategoryModel categoryModel = new CategoryModel(1, "Category 1", FrequencyPeriod.Month, 10);
        categoryModel.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(2017, Month.MAY));

        CategoryPlansViewModel categoryPlansViewModel = new CategoryPlansViewModel(
                new MonthViewModel(2017, Month.MAY),
                categoryModel,
                calculatorMock);

        // act
        binder.setupCategoriesSummaryPieChartViewData(pieChartViewMock, Arrays.asList(categoryPlansViewModel));

        // capture
        ArgumentCaptor<PieChartData> dataCaptor = ArgumentCaptor.forClass(PieChartData.class);
        verify(pieChartViewMock).setPieChartData(dataCaptor.capture());
        verify(pieChartViewMock).setChartRenderer(Matchers.notNull(ProgressPieChartRenderer.class));

        PieChartData data = dataCaptor.getValue();

        // assert before
        assertEquals(1, data.getValues().size());
        assertTrue(data.getValues().get(0) instanceof ProgressSliceValue);
        ProgressSliceValue progressSliceValue = (ProgressSliceValue) data.getValues().get(0);
        assertEquals(1f, progressSliceValue.getValue(), 0f);
        assertEquals(10f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(0f, progressSliceValue.getProgressValue(), 0f);

        // mock
        when(calculatorMock.calculatePlansSummaryForMonthInCategory(2017, Month.MAY, "Category 1"))
                .thenReturn(PlansSummaryTestHelper.createCategoryPlansSummary("Category 1", 10, 1, 0));

        // change value of category
        categoryPlansViewModel.getDailyPlansList().get(0).setStatus(PlanStatus.Success);

        // verify after
        verify(pieChartViewMock).startDataAnimation();

        // end animation explicitly
        data.finish();

        // assert after
        assertEquals(1, data.getValues().size());
        assertEquals(1f, progressSliceValue.getValue(), 0f);
        assertEquals(1f, progressSliceValue.getProgressValue(), 0f);
        assertEquals(10f, progressSliceValue.getThresholdValue(), 0f);
    }

    @Test
    public void setupPieChartViewData_updateSlicesValue_test()
    {
        // mocks
        PieChartView pieChartViewMock = mock(PieChartView.class);
        ApplicationContext applicationContextMock = getApplicationContextMock();
        PlansSummaryCalculator calculatorMock = mock(PlansSummaryCalculator.class);

        HelloChartsViewDataBinder binder = new HelloChartsViewDataBinder(applicationContextMock);

        // prepare data
        // cat 1
        PlansSummaryCalculator.CategoryPlansSummary categoryPlansSummary1
                = PlansSummaryTestHelper.createCategoryPlansSummary(getCategoryName(1), 5, 0, 0);
        when(calculatorMock.calculatePlansSummaryForMonthInCategory(2017, Month.MAY, "Category 1"))
                .thenReturn(categoryPlansSummary1);
        CategoryModel categoryModel1 = new CategoryModel(1, "Category 1", FrequencyPeriod.Month, 5);
        categoryModel1.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(2017, Month.MAY));

        CategoryPlansViewModel categoryPlansViewModel1 = new CategoryPlansViewModel(
                new MonthViewModel(2017, Month.MAY),
                categoryModel1,
                calculatorMock);

        PlansSummaryCalculator.CategoryPlansSummary categoryPlansSummary2
                = PlansSummaryTestHelper.createCategoryPlansSummary(getCategoryName(1), 5, 0, 0);
        when(calculatorMock.calculatePlansSummaryForMonthInCategory(2017, Month.MAY, "Category 2"))
                .thenReturn(categoryPlansSummary2);
        CategoryModel categoryModel2 = new CategoryModel(2, "Category 2", FrequencyPeriod.Month, 5);
        categoryModel1.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(2017, Month.MAY));

        CategoryPlansViewModel categoryPlansViewModel2 = new CategoryPlansViewModel(
                new MonthViewModel(2017, Month.MAY),
                categoryModel2,
                calculatorMock);

        // act
        binder.setupCategoriesSummaryPieChartViewData(pieChartViewMock, Arrays.asList(categoryPlansViewModel1, categoryPlansViewModel2));

        // capture
        ArgumentCaptor<PieChartData> dataCaptor = ArgumentCaptor.forClass(PieChartData.class);
        verify(pieChartViewMock).setPieChartData(dataCaptor.capture());
        verify(pieChartViewMock).setChartRenderer(Matchers.notNull(ProgressPieChartRenderer.class));

        PieChartData data = dataCaptor.getValue();

        // assert before
        assertEquals(2, data.getValues().size());
        assertTrue(data.getValues().get(0) instanceof ProgressSliceValue);
        ProgressSliceValue progressSliceValue = (ProgressSliceValue) data.getValues().get(0);
        assertEquals(0.5f, progressSliceValue.getValue(), 0f);
        assertEquals(5f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(0f, progressSliceValue.getProgressValue(), 0f);

        assertTrue(data.getValues().get(1) instanceof ProgressSliceValue);
        progressSliceValue = (ProgressSliceValue) data.getValues().get(1);
        assertEquals(0.5f, progressSliceValue.getValue(), 0f);
        assertEquals(5f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(0f, progressSliceValue.getProgressValue(), 0f);

        // mock
        when(calculatorMock.calculatePlansSummaryForMonthInCategory(2017, Month.MAY, "Category 1"))
                .thenReturn(PlansSummaryTestHelper.createCategoryPlansSummary("Category 1", 15, 0, 0));

        when(calculatorMock.calculatePlansSummaryForMonthInCategory(2017, Month.MAY, "Category 2"))
                .thenReturn(PlansSummaryTestHelper.createCategoryPlansSummary("Category 2", 5, 0, 0));

        // change value of category
        categoryPlansViewModel1.setFrequencyValue(15);

        // verify after
        verify(pieChartViewMock).startDataAnimation();

        // end animation explicitly
        data.finish();

        // assert after
        assertEquals(2, data.getValues().size());
        assertTrue(data.getValues().get(0) instanceof ProgressSliceValue);
        progressSliceValue = (ProgressSliceValue) data.getValues().get(0);
        assertEquals(0.75f, progressSliceValue.getValue(), 0f);
        assertEquals(15f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(0f, progressSliceValue.getProgressValue(), 0f);

        assertTrue(data.getValues().get(1) instanceof ProgressSliceValue);
        progressSliceValue = (ProgressSliceValue) data.getValues().get(1);
        assertEquals(0.25f, progressSliceValue.getValue(), 0f);
        assertEquals(5f, progressSliceValue.getThresholdValue(), 0f);
        assertEquals(0f, progressSliceValue.getProgressValue(), 0f);
    }

    private ApplicationContext getApplicationContextMock()
    {
        ApplicationContext applicationContextMock = mock(ApplicationContext.class);
        when(applicationContextMock.getStringFromResources(R.string.monthly_plans_summary_generalPieChart_simple_sliceLabel))
                .thenReturn("%s, %d/%d");

        Context contextMock = mock(Context.class);
        Resources resourcesMock = mock(Resources.class);
        when(resourcesMock.getDisplayMetrics()).thenReturn(new DisplayMetrics());
        when(contextMock.getResources()).thenReturn(resourcesMock);

        when(applicationContextMock.getDefaultContext()).thenReturn(contextMock);

        return applicationContextMock;
    }

    private List<CategoryPlansViewModel> createCategoryPlansViewModelList(PlansSummaryCalculator summaryCalculator, PlansSummaryCalculator.CategoryPlansSummary... summaries)
    {
        List<CategoryPlansViewModel> result = new ArrayList<>();
        MonthViewModel monthViewModel = new MonthViewModel(2017, Month.MAY);

        for(int i = 0; i < summaries.length; i++)
        {
            int id = i +1;
            String name = getCategoryName(id);

            when(summaryCalculator.calculatePlansSummaryForMonthInCategory(
                    2017,
                    Month.MAY,
                    name)).thenReturn(summaries[i]);

                    CategoryPlansViewModel vm = new CategoryPlansViewModel(monthViewModel,
                    new CategoryModel(id, name, FrequencyPeriod.Week, 1),
                    summaryCalculator);
            result.add(vm);
        }

        return result;
    }

    private String getCategoryName(int catId)
    {
        return "Category " + catId;
    }

    private void assertDefaultData(PieChartData data)
    {
        assertNotNull(data);
        assertEquals(true, data.hasLabelsOnlyForSelected());
        assertEquals(false, data.hasLabels());
        assertEquals(0, data.getSlicesSpacing());
        assertEquals(false, data.hasLabelsOutside());
        assertEquals(false, data.hasCenterCircle());
    }
}