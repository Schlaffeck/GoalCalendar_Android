package com.slamcode.goalcalendar.view.charts.data.hellocharts;

import android.view.View;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.view.PieChartView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
        ApplicationContext applicationContextMock = mock(ApplicationContext.class);

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

        // slices
        assertEquals(2, data.getValues().size());

        // cat 1
        assertEquals(0.5f, data.getValues().get(0).getValue(), 0f);
        assertEquals("Category 1, to do 2 tasks", new String(data.getValues().get(0).getLabelAsChars()));

        // cat 2
        assertEquals(0.5f, data.getValues().get(1).getValue(), 0f);
        assertEquals("Category 2, to do 2 tasks", new String(data.getValues().get(1).getLabelAsChars()));
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

        // slices
        assertEquals(4, data.getValues().size());

        // cat 1
        assertEquals(0.2f, data.getValues().get(0).getValue(), 0f);
        assertEquals("Category 1, done 2 tasks", new String(data.getValues().get(0).getLabelAsChars()));
        assertEquals(0.3f, data.getValues().get(1).getValue(), 0f);
        assertEquals("Category 1, to do 3 tasks", new String(data.getValues().get(1).getLabelAsChars()));

        // cat 2
        assertEquals(0.3f, data.getValues().get(2).getValue(), 0f);
        assertEquals("Category 2, done 3 tasks", new String(data.getValues().get(2).getLabelAsChars()));
        assertEquals(0.2f, data.getValues().get(3).getValue(), 0f);
        assertEquals("Category 2, to do 2 tasks", new String(data.getValues().get(3).getLabelAsChars()));
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

        // slices
        assertEquals(5, data.getValues().size());

        // cat 1
        assertEquals(0.2f, data.getValues().get(0).getValue(), 0f);
        assertEquals("Category 1, done 2 tasks", new String(data.getValues().get(0).getLabelAsChars()));
        assertEquals(0.1f, data.getValues().get(1).getValue(), 0f);
        assertEquals("Category 1, to do 1 tasks", new String(data.getValues().get(1).getLabelAsChars()));

        // cat 2
        assertEquals(0.3f, data.getValues().get(2).getValue(), 0f);
        assertEquals("Category 2, done 3 tasks", new String(data.getValues().get(2).getLabelAsChars()));

        // cat 3
        assertEquals(0.3f, data.getValues().get(3).getValue(), 0f);
        assertEquals("Category 3, done 3 tasks", new String(data.getValues().get(3).getLabelAsChars()));
        assertEquals(0.1f, data.getValues().get(4).getValue(), 0f);
        assertEquals("Category 3, to do 1 tasks", new String(data.getValues().get(4).getLabelAsChars()));
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

        // slices
        assertEquals(5, data.getValues().size());

        // cat 1
        assertEquals(0.222f, data.getValues().get(0).getValue(), 0.001f);
        assertEquals("Category 1, done 2 tasks", new String(data.getValues().get(0).getLabelAsChars()));
        assertEquals(0.1111f, data.getValues().get(1).getValue(), 0.001f);
        assertEquals("Category 1, overdone 1 tasks", new String(data.getValues().get(1).getLabelAsChars()));

        // cat 2
        assertEquals(0.3333f, data.getValues().get(2).getValue(), 0.001f);
        assertEquals("Category 2, done 3 tasks", new String(data.getValues().get(2).getLabelAsChars()));

        // cat 3
        assertEquals(0.3333f, data.getValues().get(3).getValue(), 0.001f);
        assertEquals("Category 3, done 3 tasks", new String(data.getValues().get(3).getLabelAsChars()));
        assertEquals(0.1111f, data.getValues().get(4).getValue(), 0.001f);
        assertEquals("Category 3, to do 1 tasks", new String(data.getValues().get(4).getLabelAsChars()));
    }

    @Test
    public void setupCategoriesSummaryPieChartViewData_updateSlicesValue_test()
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
        verify(pieChartViewMock).setVisibility(View.VISIBLE);

        PieChartData data = dataCaptor.getValue();

        // assert before
        assertEquals(1, data.getValues().size());
        assertEquals(1f, data.getValues().get(0).getValue(), 0f);

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
        assertEquals(2, data.getValues().size());
        assertEquals(0.1f, data.getValues().get(0).getValue(), 0f);
        assertEquals(0.9f, data.getValues().get(1).getValue(), 0f);
    }

    private ApplicationContext getApplicationContextMock()
    {
        ApplicationContext applicationContextMock = mock(ApplicationContext.class);
        when(applicationContextMock.getStringFromResources(R.string.monthly_plans_summary_generalPieChart_todo_sliceLabel))
                .thenReturn("%s, to do %d tasks");
        when(applicationContextMock.getStringFromResources(R.string.monthly_plans_summary_generalPieChart_done_sliceLabel))
                .thenReturn("%s, done %d tasks");
        when(applicationContextMock.getStringFromResources(R.string.monthly_plans_summary_generalPieChart_overdone_sliceLabel))
                .thenReturn("%s, overdone %d tasks");

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