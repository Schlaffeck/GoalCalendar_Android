package com.slamcode.goalcalendar.view.viewmodel;

import android.content.Context;
import android.test.mock.MockContext;
import android.view.LayoutInflater;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.goalcalendar.data.MonthlyPlansRepository;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.summary.PlansSummaryCalculator;
import com.slamcode.goalcalendar.view.CategoryDailyPlansRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.CategoryNameRecyclerViewAdapter;
import com.slamcode.goalcalendar.view.lists.ListAdapterProvider;
import com.slamcode.goalcalendar.view.presenters.MonthlyGoalsPresenter;
import com.slamcode.goalcalendar.view.presenters.PersistentMonthlyGoalsPresenter;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by moriasla on 02.02.2017.
 */
public class PersistentMonthlyGoalsPresenterTest {

    @Test
    public void monthlyGoalsViewModel_constructor_emptyList_test()
    {
        Context context = new MockContext();
        LayoutInflater inflater = mock(LayoutInflater.class);
        PersistenceContext persistenceContext = mock(PersistenceContext.class);
        PlansSummaryCalculator calculatorMock = mock(PlansSummaryCalculator.class);

        CategoryNameRecyclerViewAdapter namesAdapter = mock(CategoryNameRecyclerViewAdapter.class);
        CategoryDailyPlansRecyclerViewAdapter dailyPlansAdapter = mock(CategoryDailyPlansRecyclerViewAdapter.class);
        ListAdapterProvider adapterProvider = mock(ListAdapterProvider.class);
        when(adapterProvider.provideCategoryNameListViewAdapter(context, inflater)).thenReturn(namesAdapter);
        when(adapterProvider.provideCategoryDailyPlansListViewAdapter(context, inflater)).thenReturn(dailyPlansAdapter);

        MonthlyGoalsPresenter viewModel = new PersistentMonthlyGoalsPresenter(context, inflater, persistenceContext, adapterProvider, calculatorMock);

        assertTrue(viewModel.isEmptyCategoriesList());
        assertEquals(Month.getCurrentMonth(), viewModel.getSelectedMonth());
        assertEquals(DateTimeHelper.getCurrentYear(), viewModel.getSelectedYear());

        assertNotNull(viewModel.getCategoryNamesRecyclerViewAdapter());
        assertEquals(0, viewModel.getCategoryNamesRecyclerViewAdapter().getItemCount());
    }

    @Test
    public void monthlyGoalsViewModel_constructor_withCategoriesList_test()
    {
        // mocks
        Context context = new MockContext();
        LayoutInflater inflater = mock(LayoutInflater.class);
        PersistenceContext persistenceContext = mock(PersistenceContext.class);
        PlansSummaryCalculator calculatorMock = mock(PlansSummaryCalculator.class);

        UnitOfWork unitOfWork = mock(UnitOfWork.class);

        MonthlyPlansRepository repository = mock(MonthlyPlansRepository.class);

        MonthlyPlansModel monthlyPlansModel =
                new MonthlyPlansModel(1,
                    DateTimeHelper.getCurrentYear(),
                    Month.getCurrentMonth());
        monthlyPlansModel.setCategories(
                CollectionUtils.createList(new CategoryModel(1, "Kat 1", FrequencyPeriod.Month, 2)));

        when(persistenceContext.createUnitOfWork()).thenReturn(unitOfWork);
        when(unitOfWork.getMonthlyPlansRepository()).thenReturn(repository);
        when(repository.findForMonth(DateTimeHelper.getCurrentYear(), Month.getCurrentMonth()))
                .thenReturn(monthlyPlansModel);

        CategoryNameRecyclerViewAdapter namesAdapter = mock(CategoryNameRecyclerViewAdapter.class);
        CategoryDailyPlansRecyclerViewAdapter dailyPlansAdapter = mock(CategoryDailyPlansRecyclerViewAdapter.class);
        ListAdapterProvider adapterProvider = mock(ListAdapterProvider.class);
        when(adapterProvider.provideCategoryNameListViewAdapter(context, inflater)).thenReturn(namesAdapter);
        when(adapterProvider.provideCategoryDailyPlansListViewAdapter(context, inflater)).thenReturn(dailyPlansAdapter);

        // create
        MonthlyGoalsPresenter viewModel = new PersistentMonthlyGoalsPresenter(context, inflater, persistenceContext, adapterProvider, calculatorMock, true);

        assertFalse(viewModel.isEmptyCategoriesList());
        assertEquals(Month.getCurrentMonth(), viewModel.getSelectedMonth());
        assertEquals(DateTimeHelper.getCurrentYear(), viewModel.getSelectedYear());

        assertEquals(namesAdapter, viewModel.getCategoryNamesRecyclerViewAdapter());
        assertEquals(dailyPlansAdapter, viewModel.getCategoryDailyPlansRecyclerViewAdapter());

        // verify mocks
        verify(persistenceContext, times(1)).createUnitOfWork();
        verify(unitOfWork, times(1)).getMonthlyPlansRepository();
        verify(unitOfWork, times(1)).complete();
        verify(repository)
                .findForMonth(Matchers.eq(DateTimeHelper.getCurrentYear()), Matchers.eq(Month.getCurrentMonth()));

        verify(namesAdapter).updateMonthlyPlans(Matchers.eq(monthlyPlansModel));
        verify(dailyPlansAdapter).updateMonthlyPlans(Matchers.eq(monthlyPlansModel));
    }

    @Test
    public void monthlyGoalsViewModel_setYearAndMonth_existingInPersistenceContext_test()
    {
        // mocks
        Context context = new MockContext();
        LayoutInflater inflater = mock(LayoutInflater.class);

        CategoryNameRecyclerViewAdapter namesAdapter = mock(CategoryNameRecyclerViewAdapter.class);
        CategoryDailyPlansRecyclerViewAdapter dailyPlansAdapter = mock(CategoryDailyPlansRecyclerViewAdapter.class);
        ListAdapterProvider adapterProvider = mock(ListAdapterProvider.class);
        when(adapterProvider.provideCategoryNameListViewAdapter(context, inflater)).thenReturn(namesAdapter);
        when(adapterProvider.provideCategoryDailyPlansListViewAdapter(context, inflater)).thenReturn(dailyPlansAdapter);

        PersistenceContext persistenceContext = mock(PersistenceContext.class);;

        UnitOfWork unitOfWork = mock(UnitOfWork.class);

        MonthlyPlansRepository repository = mock(MonthlyPlansRepository.class);

        MonthlyPlansModel monthlyPlansModel =
                new MonthlyPlansModel(1,
                        DateTimeHelper.getCurrentYear(),
                        Month.getCurrentMonth());
        monthlyPlansModel.setCategories(
                CollectionUtils.createList(new CategoryModel(1, "Kat 1", FrequencyPeriod.Month, 2)));


        // arrange
        when(persistenceContext.createUnitOfWork()).thenReturn(unitOfWork);
        when(unitOfWork.getMonthlyPlansRepository()).thenReturn(repository);
        when(repository.findForMonth(2017, Month.APRIL))
                .thenReturn(monthlyPlansModel);

        PlansSummaryCalculator calculatorMock = mock(PlansSummaryCalculator.class);
        // act
        MonthlyGoalsPresenter viewModel = new PersistentMonthlyGoalsPresenter(
                context,
                inflater,
                persistenceContext,
                adapterProvider,
                calculatorMock);
        viewModel.setYearAndMonth(2017, Month.APRIL);

        // assert
        verify(persistenceContext, times(1)).createUnitOfWork();
        verify(unitOfWork, times(1)).getMonthlyPlansRepository();
        verify(unitOfWork, times(1)).complete();
        verify(repository).findForMonth(Matchers.eq(2017), Matchers.eq(Month.APRIL));

        verify(namesAdapter).updateMonthlyPlans(Matchers.eq(monthlyPlansModel));
        verify(dailyPlansAdapter).updateMonthlyPlans(Matchers.eq(monthlyPlansModel));
    }

    @Test
    public void monthlyGoalsViewModel_setYearAndMonth_noData_test()
    {
        // mocks
        Context context = new MockContext();
        LayoutInflater inflater = mock(LayoutInflater.class);

        CategoryNameRecyclerViewAdapter namesAdapter = mock(CategoryNameRecyclerViewAdapter.class);
        CategoryDailyPlansRecyclerViewAdapter dailyPlansAdapter = mock(CategoryDailyPlansRecyclerViewAdapter.class);
        ListAdapterProvider adapterProvider = mock(ListAdapterProvider.class);
        when(adapterProvider.provideCategoryNameListViewAdapter(context, inflater)).thenReturn(namesAdapter);
        when(adapterProvider.provideCategoryDailyPlansListViewAdapter(context, inflater)).thenReturn(dailyPlansAdapter);

        PersistenceContext persistenceContext = mock(PersistenceContext.class);;

        UnitOfWork unitOfWork = mock(UnitOfWork.class);

        MonthlyPlansRepository repository = mock(MonthlyPlansRepository.class);

        // arrange
        when(persistenceContext.createUnitOfWork()).thenReturn(unitOfWork);
        when(unitOfWork.getMonthlyPlansRepository()).thenReturn(repository);
        when(repository.findForMonth(2017, Month.APRIL))
                .thenReturn(null);

        PlansSummaryCalculator calculatorMock = mock(PlansSummaryCalculator.class);

        // act
        MonthlyGoalsPresenter viewModel = new PersistentMonthlyGoalsPresenter(
                context,
                inflater,
                persistenceContext,
                adapterProvider,
                calculatorMock);
        viewModel.setYearAndMonth(2017, Month.APRIL);

        // assert
        verify(persistenceContext, times(1)).createUnitOfWork();
        verify(unitOfWork, times(2)).getMonthlyPlansRepository();
        verify(unitOfWork, times(1)).complete();
        verify(repository).findForMonth(Matchers.eq(2017), Matchers.eq(Month.APRIL));
        verify(repository).add(Matchers.notNull(MonthlyPlansModel.class));
        verify(namesAdapter).updateMonthlyPlans(Matchers.notNull(MonthlyPlansModel.class));
    }


    @Test
    public void monthlyGoalsViewModel_copyFromPreviousMonth_noData_test()
    {
        // mocks
        Context context = new MockContext();
        LayoutInflater inflater = mock(LayoutInflater.class);

        CategoryNameRecyclerViewAdapter namesAdapter = mock(CategoryNameRecyclerViewAdapter.class);
        CategoryDailyPlansRecyclerViewAdapter dailyPlansAdapter = mock(CategoryDailyPlansRecyclerViewAdapter.class);
        ListAdapterProvider adapterProvider = mock(ListAdapterProvider.class);
        when(adapterProvider.provideCategoryNameListViewAdapter(context, inflater)).thenReturn(namesAdapter);
        when(adapterProvider.provideCategoryDailyPlansListViewAdapter(context, inflater)).thenReturn(dailyPlansAdapter);

        PersistenceContext persistenceContext = mock(PersistenceContext.class);;

        UnitOfWork unitOfWork = mock(UnitOfWork.class);

        MonthlyPlansRepository repository = mock(MonthlyPlansRepository.class);

        final Month nextMonthEnum = Month.getNextMonth();
        final Month currentMonthEnum = Month.getCurrentMonth();
        final int year = DateTimeHelper.getCurrentYear();

        MonthlyPlansModel currentMonth =
                new MonthlyPlansModel(1,
                        year,
                        currentMonthEnum);
        currentMonth.setCategories(
                CollectionUtils.createList(new CategoryModel(1, "Kat 1", FrequencyPeriod.Month, 2)));

        MonthlyPlansModel nextMonth =
                new MonthlyPlansModel(1,
                        year,
                        nextMonthEnum);

        // arrange
        when(persistenceContext.createUnitOfWork()).thenReturn(unitOfWork);
        when(unitOfWork.getMonthlyPlansRepository()).thenReturn(repository);
        when(repository.findForMonth(DateTimeHelper.getCurrentYear(), Month.getCurrentMonth()))
                .thenReturn(currentMonth);
        when(repository.findForMonth(DateTimeHelper.getCurrentYear(), Month.getNextMonth()))
                .thenReturn(nextMonth);
        when(repository.findLast(Matchers.any(com.android.internal.util.Predicate.class))).thenReturn(currentMonth);

        PlansSummaryCalculator calculatorMock = mock(PlansSummaryCalculator.class);

        // act
        MonthlyGoalsPresenter viewModel = new PersistentMonthlyGoalsPresenter(
                context,
                inflater,
                persistenceContext,
                adapterProvider,
                calculatorMock);
        viewModel.setYearAndMonth(DateTimeHelper.getCurrentYear(), Month.getNextMonth());
        viewModel.copyCategoriesFromPreviousMonth();

        // assert
        verify(persistenceContext, times(2)).createUnitOfWork();
        verify(unitOfWork, times(2)).getMonthlyPlansRepository();
        verify(unitOfWork, times(1)).complete();
        verify(unitOfWork, times(1)).complete(false);
        verify(repository).findForMonth(Matchers.eq(year), Matchers.eq(nextMonthEnum));
        verify(namesAdapter).updateMonthlyPlans(Matchers.eq(nextMonth));
        verify(dailyPlansAdapter).updateMonthlyPlans(Matchers.eq(nextMonth));

        verify(repository, times(1)).findLast(Matchers.isNotNull(com.android.internal.util.Predicate.class));

        ArgumentMatcher<CategoryModel> matcher = new ArgumentMatcher<CategoryModel>(){

            @Override
            public boolean matches(Object argument) {
                CategoryModel category = (CategoryModel)argument;

                assertEquals("Kat 1", category.getName());
                assertNotNull(category.getId());
                assertEquals(2, category.getFrequencyValue());
                assertEquals(FrequencyPeriod.Month, category.getPeriod());

                assertNotNull(category.getDailyPlans());
                assertEquals(nextMonthEnum.getDaysCount(year), category.getDailyPlans().size());
                return true;
            }
        };
        verify(namesAdapter, times(1)).addOrUpdateItem(Matchers.argThat(matcher));
        verify(dailyPlansAdapter, times(1)).addOrUpdateItem(Matchers.argThat(matcher));
    }
}