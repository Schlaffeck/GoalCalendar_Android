package com.slamcode.goalcalendar.view.viewmodel;

import android.content.Context;
import android.test.mock.MockContext;
import android.view.LayoutInflater;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.goalcalendar.data.CategoryRepository;
import com.slamcode.goalcalendar.data.MonthlyPlansRepository;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.view.CategoryListViewAdapter;
import com.slamcode.goalcalendar.view.lists.ListAdapterProvider;
import com.slamcode.goalcalendar.view.lists.ListViewDataAdapter;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by moriasla on 02.02.2017.
 */
public class MonthlyGoalsViewModelTest {

    @Test
    public void monthlyGoalsViewModel_constructor_emptyList_test()
    {
        Context context = new MockContext();
        LayoutInflater inflater = Mockito.mock(LayoutInflater.class);
        PersistenceContext persistenceContext = Mockito.mock(PersistenceContext.class);

        CategoryListViewAdapter adapter = Mockito.mock(CategoryListViewAdapter.class);
        ListAdapterProvider adapterProvider = Mockito.mock(ListAdapterProvider.class);
        when(adapterProvider.provideCategoryListViewAdapter(context, inflater)).thenReturn(adapter);

        MonthlyGoalsViewModel viewModel = new MonthlyGoalsViewModel(context, inflater, persistenceContext, adapterProvider);

        assertTrue(viewModel.isEmptyCategoriesList());
        assertEquals(Month.getCurrentMonth(), viewModel.getSelectedMonth());
        assertEquals(DateTimeHelper.getCurrentYear(), viewModel.getSelectedYear());

        assertNotNull(viewModel.getMonthlyPlannedCategoryListViewAdapter());
        assertEquals(0, viewModel.getMonthlyPlannedCategoryListViewAdapter().getCount());
    }

    @Test
    public void monthlyGoalsViewModel_constructor_withCategoriesList_test()
    {
        // mocks
        Context context = new MockContext();
        LayoutInflater inflater = Mockito.mock(LayoutInflater.class);
        PersistenceContext persistenceContext = Mockito.mock(PersistenceContext.class);

        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);

        MonthlyPlansRepository repository = Mockito.mock(MonthlyPlansRepository.class);

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

        CategoryListViewAdapter adapter = Mockito.mock(CategoryListViewAdapter.class);
        ListAdapterProvider adapterProvider = Mockito.mock(ListAdapterProvider.class);
        when(adapterProvider.provideCategoryListViewAdapter(context, inflater)).thenReturn(adapter);

        // create
        MonthlyGoalsViewModel viewModel = new MonthlyGoalsViewModel(context, inflater, persistenceContext, adapterProvider, true);

        assertFalse(viewModel.isEmptyCategoriesList());
        assertEquals(Month.getCurrentMonth(), viewModel.getSelectedMonth());
        assertEquals(DateTimeHelper.getCurrentYear(), viewModel.getSelectedYear());

        assertEquals(adapter, viewModel.getMonthlyPlannedCategoryListViewAdapter());

        // verify mocks
        verify(persistenceContext, times(1)).createUnitOfWork();
        verify(unitOfWork, times(1)).getMonthlyPlansRepository();
        verify(unitOfWork, times(1)).complete();
        verify(repository)
                .findForMonth(Matchers.eq(DateTimeHelper.getCurrentYear()), Matchers.eq(Month.getCurrentMonth()));

        verify(adapter).updateMonthlyPlans(Matchers.eq(monthlyPlansModel));
    }

    @Test
    public void monthlyGoalsViewModel_setYearAndMonth_existingInPersistenceContext_test()
    {
        // mocks
        Context context = new MockContext();
        LayoutInflater inflater = Mockito.mock(LayoutInflater.class);

        CategoryListViewAdapter adapter = Mockito.mock(CategoryListViewAdapter.class);
        ListAdapterProvider adapterProvider = Mockito.mock(ListAdapterProvider.class);
        when(adapterProvider.provideCategoryListViewAdapter(context, inflater)).thenReturn(adapter);

        PersistenceContext persistenceContext = Mockito.mock(PersistenceContext.class);;

        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);

        MonthlyPlansRepository repository = Mockito.mock(MonthlyPlansRepository.class);

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

        // act
        MonthlyGoalsViewModel viewModel = new MonthlyGoalsViewModel(
                context,
                inflater,
                persistenceContext,
                adapterProvider);
        viewModel.setYearAndMonth(2017, Month.APRIL);

        // assert
        verify(persistenceContext, times(1)).createUnitOfWork();
        verify(unitOfWork, times(1)).getMonthlyPlansRepository();
        verify(unitOfWork, times(1)).complete();
        verify(repository).findForMonth(Matchers.eq(2017), Matchers.eq(Month.APRIL));

        verify(adapter).updateMonthlyPlans(Matchers.eq(monthlyPlansModel));
    }

    @Test
    public void monthlyGoalsViewModel_setYearAndMonth_noData_test()
    {
        // mocks
        Context context = new MockContext();
        LayoutInflater inflater = Mockito.mock(LayoutInflater.class);

        CategoryListViewAdapter adapter = Mockito.mock(CategoryListViewAdapter.class);
        ListAdapterProvider adapterProvider = Mockito.mock(ListAdapterProvider.class);
        when(adapterProvider.provideCategoryListViewAdapter(context, inflater)).thenReturn(adapter);

        PersistenceContext persistenceContext = Mockito.mock(PersistenceContext.class);;

        UnitOfWork unitOfWork = Mockito.mock(UnitOfWork.class);

        MonthlyPlansRepository repository = Mockito.mock(MonthlyPlansRepository.class);

        // arrange
        when(persistenceContext.createUnitOfWork()).thenReturn(unitOfWork);
        when(unitOfWork.getMonthlyPlansRepository()).thenReturn(repository);
        when(repository.findForMonth(2017, Month.APRIL))
                .thenReturn(null);

        // act
        MonthlyGoalsViewModel viewModel = new MonthlyGoalsViewModel(
                context,
                inflater,
                persistenceContext,
                adapterProvider);
        viewModel.setYearAndMonth(2017, Month.APRIL);

        // assert
        verify(persistenceContext, times(1)).createUnitOfWork();
        verify(unitOfWork, times(2)).getMonthlyPlansRepository();
        verify(unitOfWork, times(1)).complete();
        verify(repository).findForMonth(Matchers.eq(2017), Matchers.eq(Month.APRIL));
        verify(repository).add(Matchers.notNull(MonthlyPlansModel.class));
        verify(adapter).updateMonthlyPlans(Matchers.notNull(MonthlyPlansModel.class));
    }
}