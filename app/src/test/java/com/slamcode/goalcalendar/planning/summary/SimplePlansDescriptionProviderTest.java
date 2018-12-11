package com.slamcode.goalcalendar.planning.summary;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.TestUtils;
import com.slamcode.goalcalendar.data.CategoriesRepository;
import com.slamcode.goalcalendar.data.model.plans.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.planning.DateTime;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by smoriak on 29/05/2017.
 */
public class SimplePlansDescriptionProviderTest {

    @Test
    public void provideDescriptionForMonthInCategory_noDataAvailable_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(new ArrayList<CategoryModel>());

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 1));

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals(null, actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
    }

    @Test
    public void provideDescriptionForMonthInCategory_nothingDone_beginningOfMonth_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName);

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 1));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC=0", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_eq_zero);
    }

    @Test
    public void provideDescriptionForMonthInCategory_nothingDone_monthProgressLessThanThreshold_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        // nothing done
        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 0);

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 4));
        // less than 10% of month passed
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM>PC=0", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_gt_progressCategory_eq_zero);
    }

    @Test
    public void provideDescriptionForMonthInCategory_nothingDone_monthProgressMoreThanThreshold_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        // nothing done
        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 0);

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 5));
        // more than 10% of month passed, nothing done still
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM>PC=0", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_gt_progressCategory_eq_zero);
    }

    @Test
    public void provideDescriptionForMonthInCategory_10PercentDone_monthProgressLessThanThreshold_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 2);
        // 2 of 20 done (10%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // ~9% of month passed -> ~9% < 10% threshold
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 3));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC<>0", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_lt_progressCategory_eq_zero);
    }

    @Test
    public void provideDescriptionForMonthInCategory_doneMoreThanPlanned_lessThanThreshold_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 4);
        // 4 of 20 done (20%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // ~16% of month passed -> 20%-16% = 4% < 10% threshold
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 6));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC<>0", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_neq_zero);
    }

    @Test
    public void provideDescriptionForMonthInCategory_doneMoreThanPlanned_moreThanThreshold_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 7);
        // 4 of 20 done (20%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // ~22% of month passed -> 22% - 35% = -13% > -10% threshold
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 7));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM<PC<>0", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_lt_progressCategory_neq_zero);
    }

    @Test
    public void provideDescriptionForMonthInCategory_allDone_monthFinished_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 20);
        // 20 of 20 done (100%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // month passed
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 31));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC=1", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_done);
    }

    @Test
    public void provideDescriptionForMonthInCategory_allDone_moreThanThresholdTillEndOfMonth_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 20);
        // 20 of 20 done (100%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // still 6/31 = ~19% till the eond of week > 10% threshold
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 25));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM<PC=1", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_doneBeforeEndOfMonth);
    }

    @Test
    public void provideDescriptionForMonthInCategory_overDone_almostEndOfMonth_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 21);
        // 21 of 20 done (1005%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // still 4/31 = ~12% till the end of week > 10% threshold
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 28));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM<PC>1", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_overDoneBeforeEndOfMonth);
    }

    @Test
    public void provideDescriptionForMonthInCategory_overDone_endOfMonth_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 21);
        // 21 of 20 done (1005%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // still 3/31 = ~9% till the end of week < 10% threshold
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 31));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM<PC>1", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_overDone);
    }

    @Test
    public void provideDescriptionForMonthInCategory_notDone_endOfMonth_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 17);
        // 17 of 20 done (85%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // still 3/31 = ~9% till the end of week < 10% threshold
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 31));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC<1", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_notDone);
    }

    @Test
    public void provideDescriptionForMonthInCategory_notDone_almostEndOfMonth_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 17);
        // 17 of 20 done (85%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // still 3/31 = ~9% till the end of week < 10% threshold
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 28));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM>PC<1", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_notDoneBeforeEndOfMonth);
    }

    @Test
    public void provideDescriptionForMonthInCategory_monthPassed_allDone_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 20);
        // 20 of 20 done (100%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // month passed
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, Month.JUNE, 1));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC=1", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_done);
    }

    @Test
    public void provideDescriptionForMonthInCategory_monthPassed_notDone_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 12);
        // 20 of 20 done (100%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // month passed
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, Month.JUNE, 1));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC<1", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_notDone);
    }

    @Test
    public void provideDescriptionForMonthInCategory_monthPassed_nothingDone_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 0);
        // 20 of 20 done (100%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // month passed
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, Month.JUNE, 1));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC<1", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_notDone);
    }

    @Test
    public void provideDescriptionForMonthInCategory_monthNotYetStarted_nothingDone_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 0);
        // 20 of 20 done (100%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // month passed
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, Month.APRIL, 28));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC=?", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_monthNotStartedYet);
    }

    @Test
    public void provideDescriptionForMonthInCategory_monthNotYetStarted_somethingDone_test()
    {
        int year = 2017;
        Month month = Month.MAY;
        String categoryName = "Cat 1";

        CategoryModel categoryModel = createCategoryModel(year, month, categoryName, FrequencyPeriod.Month, 20, 1);
        // 20 of 20 done (100%)

        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonthWithName(year, month, categoryName)).thenReturn(Arrays.asList(categoryModel));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        // month passed
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, Month.APRIL, 28));
        this.mockApplicationContextReturningStringsForCategory(applicationContext);

        SimplePlansDescriptionProvider provider = new SimplePlansDescriptionProvider(applicationContext, repository);

        String actualDescription = provider.provideDescriptionMonthInCategory(year, month, categoryName);

        assertEquals("PM=PC=!", actualDescription);

        verify(repository).findForMonthWithName(year, month, categoryName);
        verify(applicationContext).getDateTimeNow();
        verify(applicationContext).getStringFromResources(R.string.monthly_plans_summary_category_description_monthNotStartedYet_somethingDone);
    }

    @Test
    public void provideDescriptionForMonth_noCategories_test(){

        int year = 2017;
        Month month = Month.JUNE;

        // arrange mocks
        CategoriesRepository repository = mock(CategoriesRepository.class);
        when(repository.findForMonth(year, month)).thenReturn(new ArrayList<CategoryModel>());

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        mockApplicationContextReturningStringsForMonth(applicationContext);

        SimplePlansDescriptionProvider descriptionProvider = new SimplePlansDescriptionProvider(applicationContext, repository);

        // act
        PlansSummaryDescriptionProvider.PlansSummaryDescription description = descriptionProvider.provideDescriptionForMonth(year, month);

        // assert
        assertNotNull(description);
        assertEquals("No categories", description.getTitle());
        assertEquals("No categories description", description.getDetails());
        verify(repository, times(1)).findForMonth(year, month);
    }

    @Test
    public void provideDescriptionForMonth_nothingStarted_monthJustStarted_test(){

        int year = 2017;
        Month month = Month.JUNE;

        // arrange mocks
        CategoriesRepository repository = mock(CategoriesRepository.class);
        CategoryModel cat1 = new CategoryModel(1, "C1", FrequencyPeriod.Month, 20);
        CategoryModel cat2 = new CategoryModel(2, "C2", FrequencyPeriod.Month, 20);
        when(repository.findForMonth(year, month)).thenReturn(Arrays.asList(cat1, cat2));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        mockApplicationContextReturningStringsForMonth(applicationContext);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 1));

        SimplePlansDescriptionProvider descriptionProvider = new SimplePlansDescriptionProvider(applicationContext, repository);

        // act
        PlansSummaryDescriptionProvider.PlansSummaryDescription description = descriptionProvider.provideDescriptionForMonth(year, month);

        // assert
        assertNotNull(description);
        assertEquals("Month just started", description.getTitle());
        assertEquals("Plan your activities", description.getDetails());
        verify(repository, times(1)).findForMonth(year, month);
        verify(applicationContext, times(1)).getDateTimeNow();
    }

    @Test
    public void provideDescriptionForMonth_nothingStarted_monthInProgress_test(){

        int year = 2017;
        Month month = Month.JUNE;

        // arrange mocks
        CategoriesRepository repository = mock(CategoriesRepository.class);
        CategoryModel cat1 = new CategoryModel(1, "C1", FrequencyPeriod.Month, 20);
        CategoryModel cat2 = new CategoryModel(2, "C2", FrequencyPeriod.Month, 20);
        when(repository.findForMonth(year, month)).thenReturn(Arrays.asList(cat1, cat2));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        mockApplicationContextReturningStringsForMonth(applicationContext);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 2));

        SimplePlansDescriptionProvider descriptionProvider = new SimplePlansDescriptionProvider(applicationContext, repository);

        // act
        PlansSummaryDescriptionProvider.PlansSummaryDescription description = descriptionProvider.provideDescriptionForMonth(year, month);

        // assert
        assertNotNull(description);
        assertEquals("Nothing done yet", description.getTitle());
        assertEquals("Start realising your plans", description.getDetails());
        verify(repository, times(1)).findForMonth(year, month);
        verify(applicationContext, times(1)).getDateTimeNow();
    }

    @Test
    public void provideDescriptionForMonth_oneCategoryNotStarted_monthInProgress_test(){

        int year = 2017;
        Month month = Month.JUNE;

        // arrange mocks
        CategoriesRepository repository = mock(CategoriesRepository.class);
        CategoryModel cat1 = new CategoryModel(1, "C1", FrequencyPeriod.Month, 20);
        CategoryModel cat2 = TestUtils.createCategoryModel(year, month, 2, "C1", FrequencyPeriod.Month, 20, 4);
        when(repository.findForMonth(year, month)).thenReturn(Arrays.asList(cat1, cat2));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        mockApplicationContextReturningStringsForMonth(applicationContext);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, 5));

        SimplePlansDescriptionProvider descriptionProvider = new SimplePlansDescriptionProvider(applicationContext, repository);

        // act
        PlansSummaryDescriptionProvider.PlansSummaryDescription description = descriptionProvider.provideDescriptionForMonth(year, month);

        // assert
        assertNotNull(description);
        assertEquals("Category not started yet", description.getTitle());
        assertEquals("Maybe make today some progress in C1 category", description.getDetails());
        verify(repository, times(1)).findForMonth(year, month);
        verify(applicationContext, times(1)).getDateTimeNow();
    }

    @Test
    public void provideDescriptionForMonth_fewCategoriesNotStarted_monthInProgress_moreThanNoOfMissedCategories_test(){

        int year = 2017;
        Month month = Month.JUNE;
        int day = 4;

        // arrange mocks
        CategoriesRepository repository = mock(CategoriesRepository.class);
        CategoryModel cat1 = new CategoryModel(1, "C1", FrequencyPeriod.Month, 20);
        CategoryModel cat2 = TestUtils.createCategoryModel(year, month, 2, "C1", FrequencyPeriod.Month, 20, 4);
        CategoryModel cat3 = new CategoryModel(3, "C3", FrequencyPeriod.Month, 20);
        CategoryModel cat4 = new CategoryModel(4, "C4", FrequencyPeriod.Month, 20);
        CategoryModel cat5 = TestUtils.createCategoryModel(year, month, 5, "C5", FrequencyPeriod.Month, 20, 3);
        when(repository.findForMonth(year, month)).thenReturn(Arrays.asList(cat1, cat2, cat3, cat4, cat5));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        mockApplicationContextReturningStringsForMonth(applicationContext);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, day));

        SimplePlansDescriptionProvider descriptionProvider = new SimplePlansDescriptionProvider(applicationContext, repository);

        // act
        PlansSummaryDescriptionProvider.PlansSummaryDescription description = descriptionProvider.provideDescriptionForMonth(year, month);

        // assert
        assertNotNull(description);
        assertEquals("Do not forget those categories", description.getTitle());
        assertEquals("You still have 3 categories to progress in this month", description.getDetails());
        verify(repository, times(1)).findForMonth(year, month);
        verify(applicationContext, times(1)).getDateTimeNow();
    }

    @Test
    public void provideDescriptionForMonth_progressSlower_monthInProgress_test(){

        int year = 2017;
        Month month = Month.JUNE;
        int day = 11;

        // arrange mocks
        CategoriesRepository repository = mock(CategoriesRepository.class);
        // 20 of 100 all tasks done (20%) -> 11th day (33% of month)
        // more than 10% month progress over tasks progress
        CategoryModel cat1 = TestUtils.createCategoryModel(year, month, 1, "C1", FrequencyPeriod.Month, 20, 2);
        CategoryModel cat2 = TestUtils.createCategoryModel(year, month, 2, "C2", FrequencyPeriod.Month, 20, 8);
        CategoryModel cat3 = TestUtils.createCategoryModel(year, month, 3, "C3", FrequencyPeriod.Month, 20, 3);
        CategoryModel cat4 = TestUtils.createCategoryModel(year, month, 4, "C4", FrequencyPeriod.Month, 20, 2);
        CategoryModel cat5 = TestUtils.createCategoryModel(year, month, 5, "C5", FrequencyPeriod.Month, 20, 5);
        when(repository.findForMonth(year, month)).thenReturn(Arrays.asList(cat1, cat2, cat3, cat4, cat5));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        mockApplicationContextReturningStringsForMonth(applicationContext);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, day));

        SimplePlansDescriptionProvider descriptionProvider = new SimplePlansDescriptionProvider(applicationContext, repository);

        // act
        PlansSummaryDescriptionProvider.PlansSummaryDescription description = descriptionProvider.provideDescriptionForMonth(year, month);

        // assert
        assertNotNull(description);
        assertEquals("You are little behind with plans", description.getTitle());
        assertEquals("Maybe you can do something more this next days", description.getDetails());
        verify(repository, times(1)).findForMonth(year, month);
        verify(applicationContext, times(1)).getDateTimeNow();
    }

    @Test
    public void provideDescriptionForMonth_progressRegular_monthInProgress_test(){

        int year = 2017;
        Month month = Month.JUNE;
        int day = 10;

        // arrange mocks
        CategoriesRepository repository = mock(CategoriesRepository.class);
        // 30 of 100 all tasks done (30%) -> 10th day (33% of month)
        // tasks progress in boundaries (+- 10 percentage points)
        CategoryModel cat1 = TestUtils.createCategoryModel(year, month, 1, "C1", FrequencyPeriod.Month, 20, 2);
        CategoryModel cat2 = TestUtils.createCategoryModel(year, month, 2, "C2", FrequencyPeriod.Month, 20, 8);
        CategoryModel cat3 = TestUtils.createCategoryModel(year, month, 3, "C3", FrequencyPeriod.Month, 20, 14);
        CategoryModel cat4 = TestUtils.createCategoryModel(year, month, 4, "C4", FrequencyPeriod.Month, 20, 1);
        CategoryModel cat5 = TestUtils.createCategoryModel(year, month, 5, "C5", FrequencyPeriod.Month, 20, 5);
        when(repository.findForMonth(year, month)).thenReturn(Arrays.asList(cat1, cat2, cat3, cat4, cat5));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        mockApplicationContextReturningStringsForMonth(applicationContext);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, day));

        SimplePlansDescriptionProvider descriptionProvider = new SimplePlansDescriptionProvider(applicationContext, repository);

        // act
        PlansSummaryDescriptionProvider.PlansSummaryDescription description = descriptionProvider.provideDescriptionForMonth(year, month);

        // assert
        assertNotNull(description);
        assertEquals("Everything goes as planned", description.getTitle());
        assertEquals("Check what can you do next days", description.getDetails());
        verify(repository, times(1)).findForMonth(year, month);
        verify(applicationContext, times(1)).getDateTimeNow();
    }

    @Test
    public void provideDescriptionForMonth_progressFaster_monthInProgress_test(){

        int year = 2017;
        Month month = Month.JUNE;
        int day = 6;

        // arrange mocks
        CategoriesRepository repository = mock(CategoriesRepository.class);
        // 31 of 100 all tasks done (31%) -> 6th day (~20% of month)
        // tasks progress more than month progress (over 10 percentage points)
        CategoryModel cat1 = TestUtils.createCategoryModel(year, month, 1, "C1", FrequencyPeriod.Month, 20, 2);
        CategoryModel cat2 = TestUtils.createCategoryModel(year, month, 2, "C2", FrequencyPeriod.Month, 20, 11);
        CategoryModel cat3 = TestUtils.createCategoryModel(year, month, 3, "C3", FrequencyPeriod.Month, 20, 12);
        CategoryModel cat4 = TestUtils.createCategoryModel(year, month, 4, "C4", FrequencyPeriod.Month, 20, 1);
        CategoryModel cat5 = TestUtils.createCategoryModel(year, month, 5, "C5", FrequencyPeriod.Month, 20, 5);
        when(repository.findForMonth(year, month)).thenReturn(Arrays.asList(cat1, cat2, cat3, cat4, cat5));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        mockApplicationContextReturningStringsForMonth(applicationContext);
        when(applicationContext.getDateTimeNow()).thenReturn(new DateTime(year, month, day));

        SimplePlansDescriptionProvider descriptionProvider = new SimplePlansDescriptionProvider(applicationContext, repository);

        // act
        PlansSummaryDescriptionProvider.PlansSummaryDescription description = descriptionProvider.provideDescriptionForMonth(year, month);

        // assert
        assertNotNull(description);
        assertEquals("You are ahead in plans, keep on the good work", description.getTitle());
        assertEquals("You are progressing faster than planned", description.getDetails());
        verify(repository, times(1)).findForMonth(year, month);
        verify(applicationContext, times(1)).getDateTimeNow();
    }

    private void mockApplicationContextReturningStringsForMonth(ApplicationContext applicationContext) {
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_noCategories_title))
                .thenReturn("No categories");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_noCategories_description))
                .thenReturn("No categories description");

        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_monthJustStarted_title))
                .thenReturn("Month just started");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_monthJustStarted_description))
                .thenReturn("Plan your activities");

        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_nothingDone_title))
                .thenReturn("Nothing done yet");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_nothingDone_description))
                .thenReturn("Start realising your plans");

        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_oneCategoryNotStarted_title))
                .thenReturn("Category not started yet");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_oneCategoryNotStarted_description))
                .thenReturn("Maybe make today some progress in %s category");

        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_someCategoriesNotStarted_title))
                .thenReturn("Do not forget those categories");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_2To4CategoriesNotStarted_description))
                .thenReturn("You still have %d categories to progress in this month");

        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_someCategoriesNotStarted_notPlanned_title))
                .thenReturn("Do not forget those categories");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_2To4CategoriesNotStarted_notPlanned_description))
                .thenReturn("You still have %d categories to progress in this month");

        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_progressSlower_title))
                .thenReturn("You are little behind with plans");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_progressSlower_description))
                .thenReturn("Maybe you can do something more this next days");

        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_progressRegular_title))
                .thenReturn("Everything goes as planned");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_progressRegular_description))
                .thenReturn("Check what can you do next days");

        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_progressFaster_title))
                .thenReturn("You are ahead in plans, keep on the good work");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_progressMonth_progressFaster_description))
                .thenReturn("You are progressing faster than planned");
    }

    private void mockApplicationContextReturningStringsForCategory(ApplicationContext applicationContext)
    {
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_done))
                .thenReturn("PM=PC=1");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_notDoneBeforeEndOfMonth))
                .thenReturn("PM>PC<1");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_notDone))
                .thenReturn("PM=PC<1");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_overDone))
                .thenReturn("PM<PC>1");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_doneBeforeEndOfMonth))
                .thenReturn("PM<PC=1");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_lt_progressCategory_neq_zero))
                .thenReturn("PM<PC<>0");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_neq_zero))
                .thenReturn("PM=PC<>0");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_gt_progressCategory_eq_zero))
                .thenReturn("PM>PC=0");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_eq_progressCategory_eq_zero))
                .thenReturn("PM=PC=0");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressCategory_overDoneBeforeEndOfMonth))
                .thenReturn("PM<PC>1");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_progressMonth_lt_progressCategory_eq_zero))
                .thenReturn("PM=PC<>0");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_monthNotStartedYet))
                .thenReturn("PM=PC=?");
        when(applicationContext.getStringFromResources(R.string.monthly_plans_summary_category_description_monthNotStartedYet_somethingDone))
                .thenReturn("PM=PC=!");
    }

    private CategoryModel createCategoryModel(int year, Month month, String categoryName)
    {
        return createCategoryModel(year, month, categoryName, FrequencyPeriod.Week, 1, 0);
    }

    private CategoryModel createCategoryModel(int year, Month month, String categoryName, FrequencyPeriod frequencyPeriod, int frequencyRatio, int noOfSuccessfulTasks)
    {
        CategoryModel categoryModel = new CategoryModel(1, categoryName, frequencyPeriod, frequencyRatio);
        categoryModel.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, month));

        for(int i =0; i<noOfSuccessfulTasks; i++)
        {
            categoryModel.getDailyPlans().get(i).setStatus(PlanStatus.Success);
        }
        return categoryModel;
    }
}