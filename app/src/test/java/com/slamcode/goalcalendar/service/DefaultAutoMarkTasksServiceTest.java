package com.slamcode.goalcalendar.service;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.goalcalendar.data.CategoriesRepository;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.UnitOfWork;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.PlanStatus;
import com.slamcode.goalcalendar.settings.AppSettingsManager;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Calendar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by moriasla on 10.02.2017.
 */
public class DefaultAutoMarkTasksServiceTest {

    @Test
    public void defaultAutoMarkTasksService_markUnfinishedTasksAsFailed_nothingPlannedYesterday() throws Exception {

        DefaultAutoMarkTasksService service = new DefaultAutoMarkTasksService(mock(PersistenceContext.class), mock(AppSettingsManager.class));

        // mocking actions

        when(service.settingsManager.getAutomaticallyMarkUncompletedTask()).thenReturn(true);

        UnitOfWork uowMock = mock(UnitOfWork.class);
        when(service.persistenceContext.createUnitOfWork()).thenReturn(uowMock);

        CategoriesRepository categoriesRepositoryMock = mock(CategoriesRepository.class);
        when(uowMock.getCategoriesRepository()).thenReturn(categoriesRepositoryMock);

        DateTime yesterday = DateTimeHelper.getYesterdayDateTime();
        when(categoriesRepositoryMock.findForDateWithStatus(yesterday.getYear(), yesterday.getMonth(), yesterday.getDay(), PlanStatus.Planned))
        .thenReturn(CollectionUtils.<CategoryModel>emptyList());

        // run method
        AutoMarkTasksService.AutoMarkResult result = service.markUnfinishedTasksAsFailed();
        assertTrue(result.getWasRun());
        assertEquals(0, result.getUnfinishedTasksMarkedFailedCount());

        // verify mocks
        verify(service.settingsManager).getAutomaticallyMarkUncompletedTask();
        verify(service.persistenceContext).createUnitOfWork();
        verify(uowMock).getCategoriesRepository();
        verify(categoriesRepositoryMock).findForDateWithStatus(yesterday.getYear(), yesterday.getMonth(), yesterday.getDay(), PlanStatus.Planned);
        verify(uowMock).complete();
    }

    @Test
    public void defaultAutoMarkTasksService_markUnfinishedTasksAsFailed_settingOff_notRun() throws Exception {

        DefaultAutoMarkTasksService service = new DefaultAutoMarkTasksService(mock(PersistenceContext.class), mock(AppSettingsManager.class));

        // mocking actions

        when(service.settingsManager.getAutomaticallyMarkUncompletedTask()).thenReturn(false);

        // run method
        AutoMarkTasksService.AutoMarkResult result = service.markUnfinishedTasksAsFailed();
        assertFalse(result.getWasRun());
        assertEquals(0, result.getUnfinishedTasksMarkedFailedCount());

        // verify mocks
        verify(service.settingsManager).getAutomaticallyMarkUncompletedTask();
        verify(service.persistenceContext, never()).createUnitOfWork();
        verify(service.persistenceContext, never()).persistData();
    }

    @Test
    public void defaultAutoMarkTasksService_markUnfinishedTasksAsFailed_multipleThingsPlanned() throws Exception {

        DefaultAutoMarkTasksService service = new DefaultAutoMarkTasksService(mock(PersistenceContext.class), mock(AppSettingsManager.class));

        // mocking actions

        when(service.settingsManager.getAutomaticallyMarkUncompletedTask()).thenReturn(true);

        UnitOfWork uowMock = mock(UnitOfWork.class);
        when(service.persistenceContext.createUnitOfWork()).thenReturn(uowMock);

        CategoriesRepository categoriesRepositoryMock = mock(CategoriesRepository.class);
        when(uowMock.getCategoriesRepository()).thenReturn(categoriesRepositoryMock);

        DateTime yesterday = DateTimeHelper.getYesterdayDateTime();
        CategoryModel c1 = new CategoryModel(1, "C1", FrequencyPeriod.Week, 1);
                c1.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(yesterday.getYear(), yesterday.getMonth()));
                c1.getDailyPlans().get(yesterday.getDay() -1).setStatus(PlanStatus.Planned);

        CategoryModel c2 = new CategoryModel(2, "C2", FrequencyPeriod.Week, 1);
                c2.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(yesterday.getYear(), yesterday.getMonth()));
                c2.getDailyPlans().get(yesterday.getDay() -1).setStatus(PlanStatus.Planned);

        CategoryModel c3 = new CategoryModel(3, "C3", FrequencyPeriod.Week, 1);
                c3.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(yesterday.getYear(), yesterday.getMonth()));
                c3.getDailyPlans().get(yesterday.getDay() -1).setStatus(PlanStatus.Planned);

        when(categoriesRepositoryMock.findForDateWithStatus(yesterday.getYear(), yesterday.getMonth(), yesterday.getDay(), PlanStatus.Planned))
                .thenReturn(CollectionUtils.createList(c1, c2, c3));

        // run method
        AutoMarkTasksService.AutoMarkResult result = service.markUnfinishedTasksAsFailed();
        assertTrue(result.getWasRun());
        assertEquals(3, result.getUnfinishedTasksMarkedFailedCount());

        // check status was changed
        assertEquals(PlanStatus.Failure, c1.getDailyPlans().get(yesterday.getDay()-1).getStatus());
        assertEquals(PlanStatus.Failure, c2.getDailyPlans().get(yesterday.getDay()-1).getStatus());
        assertEquals(PlanStatus.Failure, c3.getDailyPlans().get(yesterday.getDay()-1).getStatus());

        // verify mocks
        verify(service.settingsManager).getAutomaticallyMarkUncompletedTask();
        verify(service.persistenceContext).createUnitOfWork();
        verify(uowMock).getCategoriesRepository();
        verify(categoriesRepositoryMock).findForDateWithStatus(yesterday.getYear(), yesterday.getMonth(), yesterday.getDay(), PlanStatus.Planned);
        verify(uowMock).complete();
    }
}