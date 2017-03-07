package com.slamcode.goalcalendar.data.inmemory;

import com.android.internal.util.Predicate;
import com.slamcode.goalcalendar.data.CategoriesRepository;
import com.slamcode.goalcalendar.data.model.CategoryModel;

import com.slamcode.collections.*;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by moriasla on 03.01.2017.
 */
public class InMemoryCategoriesRepositoryTest {

    @Test
    public void inMemoryCategoriesRepository_findAll_test()
    {
        CategoriesRepository repo = new InMemoryCategoriesRepository();

        List<CategoryModel> all = repo.findAll();
        assertEquals(0, all.size());

        repo.add(new CategoryModel(1));

        all = repo.findAll();
        assertEquals(1, all.size());
        assertEquals(1, (int)all.get(0).getId());
    }

    @Test
    public void inMemoryCategoriesRepository_findById_test()
    {
        CategoryModel cm1 = new CategoryModel(1);
        CategoryModel cm2 = new CategoryModel(2);
        CategoryModel cm3 = new CategoryModel(3);

        MonthlyPlansModel monthlyPlansModel = new MonthlyPlansModel();
        monthlyPlansModel.setCategories(Arrays.asList(cm1, cm2, cm3));
        CategoriesRepository repo
                = new InMemoryCategoriesRepository(Arrays.asList(monthlyPlansModel));

        List<CategoryModel> all = repo.findAll();
        assertEquals(3, all.size());
        assertEquals(cm1, all.get(0));
        assertEquals(cm2, all.get(1));
        assertEquals(cm3, all.get(2));

        CategoryModel model = repo.findById(0);
        assertNull(model);

        model = repo.findById(1);
        assertSame(cm1, model);

        model = repo.findById(2);
        assertSame(cm2, model);

        model = repo.findById(3);
        assertSame(cm3, model);
    }

    @Test
    public void inMemoryCategoriesRepository_remove_test()
    {
        CategoryModel cm1 = new CategoryModel(1);
        CategoryModel cm2 = new CategoryModel(2);
        CategoryModel cm3 = new CategoryModel(3);
        MonthlyPlansModel monthlyPlansModel = new MonthlyPlansModel();
        monthlyPlansModel.setCategories(Arrays.asList(cm1, cm2, cm3));
        CategoriesRepository repo
                = new InMemoryCategoriesRepository(Arrays.asList(monthlyPlansModel));

        List<CategoryModel> all = repo.findAll();
        assertEquals(3, all.size());

        repo.remove(cm1);
        assertEquals(2, repo.findAll().size());
        assertNull(repo.findById(cm1.getId()));

        repo.remove(cm3);
        assertEquals(1, repo.findAll().size());
        assertNull(repo.findById(cm3.getId()));

        repo.remove(cm2);
        assertEquals(0, repo.findAll().size());
        assertNull(repo.findById(cm2.getId()));
    }

    @Test
    public void inMemoryCategoriesRepository_findMany_test()
    {
        CategoryModel cm1 = new CategoryModel(1);
        cm1.setFrequencyValue(2);
        cm1.setPeriod(FrequencyPeriod.Week);
        cm1.setName("Category 1 - XYZ");

        CategoryModel cm2 = new CategoryModel(2);
        cm2.setFrequencyValue(3);
        cm2.setPeriod(FrequencyPeriod.Month);
        cm2.setName("Category 2 - do stuff");

        CategoryModel cm3 = new CategoryModel(3);
        cm3.setFrequencyValue(1);
        cm3.setPeriod(FrequencyPeriod.Week);
        cm3.setName("Cat 3 - Do more");
        cm3.setDailyPlans(CollectionUtils.createList(new DailyPlanModel()));

        MonthlyPlansModel monthlyPlansModel = new MonthlyPlansModel();
        monthlyPlansModel.setCategories(Arrays.asList(cm1, cm2, cm3));
        CategoriesRepository repo
                = new InMemoryCategoriesRepository(Arrays.asList(monthlyPlansModel));

        List<CategoryModel> all = repo.findAll();
        assertEquals(3, all.size());

        // find all with non-empty daily plans
        List<CategoryModel> filtered = repo.findMany(new Predicate<CategoryModel>() {
            @Override
            public boolean apply(CategoryModel categoryModel) {
                return categoryModel.getDailyPlans().size() > 0;
            }
        });

        assertEquals(1, filtered.size());
        assertEquals(cm3, filtered.get(0));

        // find all with category name with 'do'
        filtered = repo.findMany(new Predicate<CategoryModel>() {
            @Override
            public boolean apply(CategoryModel categoryModel) {
                return categoryModel.getName().toLowerCase().contains("do");
            }
        });

        assertEquals(2, filtered.size());
        assertSame(cm2, filtered.get(0));
        assertSame(cm3, filtered.get(1));

        // find all with freq period 'Week'
        filtered = repo.findMany(new Predicate<CategoryModel>() {
            @Override
            public boolean apply(CategoryModel categoryModel) {
                return categoryModel.getPeriod() == FrequencyPeriod.Week;
            }
        });

        assertEquals(2, filtered.size());
        assertSame(cm1, filtered.get(0));
        assertSame(cm3, filtered.get(1));
    }

    @Test
    public void inMemoryCategoriesRepository_findFirst_test()
    {
        CategoryModel cm1 = new CategoryModel(1);
        cm1.setFrequencyValue(2);
        cm1.setPeriod(FrequencyPeriod.Week);
        cm1.setName("Category 1 - XYZ");

        CategoryModel cm2 = new CategoryModel(2);
        cm2.setFrequencyValue(3);
        cm2.setPeriod(FrequencyPeriod.Month);
        cm2.setName("Category 2 - do stuff");

        CategoryModel cm3 = new CategoryModel(3);
        cm3.setFrequencyValue(1);
        cm3.setPeriod(FrequencyPeriod.Week);
        cm3.setName("Cat 3 - Do more");
        cm3.setDailyPlans(CollectionUtils.createList(new DailyPlanModel()));

        MonthlyPlansModel monthlyPlansModel = new MonthlyPlansModel();
        monthlyPlansModel.setCategories(Arrays.asList(cm1, cm2, cm3));
        CategoriesRepository repo
                = new InMemoryCategoriesRepository(Arrays.asList(monthlyPlansModel));

        List<CategoryModel> all = repo.findAll();
        assertEquals(3, all.size());

        // find first with non-empty daily plans
        CategoryModel filtered = repo.findFirst(new Predicate<CategoryModel>() {
            @Override
            public boolean apply(CategoryModel categoryModel) {
                return categoryModel.getDailyPlans().size() > 0;
            }
        });

        assertEquals(cm3, filtered);

        // find first with given name
        filtered = repo.findFirst(new Predicate<CategoryModel>() {
            @Override
            public boolean apply(CategoryModel categoryModel) {
                return categoryModel.getName().equals("Category 1 - XYZ");
            }
        });

        assertEquals(cm1, filtered);

        // find first with given frequency value
        filtered = repo.findFirst(new Predicate<CategoryModel>() {
            @Override
            public boolean apply(CategoryModel categoryModel) {
                return categoryModel.getFrequencyValue() >= 2;
            }
        });

        assertEquals(cm1, filtered);

        // find first with given frequency value
        filtered = repo.findFirst(new Predicate<CategoryModel>() {
            @Override
            public boolean apply(CategoryModel categoryModel) {
                return categoryModel.getPeriod() == FrequencyPeriod.Month;
            }
        });

        assertEquals(cm2, filtered);
    }

    @Test
    public void inMemoryCategoriesRepository_findLast_test()
    {
        CategoryModel cm1 = new CategoryModel(1);
        cm1.setFrequencyValue(2);
        cm1.setPeriod(FrequencyPeriod.Week);
        cm1.setName("Category 1 - XYZ");

        CategoryModel cm2 = new CategoryModel(2);
        cm2.setFrequencyValue(3);
        cm2.setPeriod(FrequencyPeriod.Month);
        cm2.setName("Category 2 - do stuff");

        CategoryModel cm3 = new CategoryModel(3);
        cm3.setFrequencyValue(1);
        cm3.setPeriod(FrequencyPeriod.Week);
        cm3.setName("Cat 3 - Do more");
        cm3.setDailyPlans(CollectionUtils.createList(new DailyPlanModel()));

        MonthlyPlansModel monthlyPlansModel = new MonthlyPlansModel();
        monthlyPlansModel.setCategories(Arrays.asList(cm1, cm2, cm3));
        CategoriesRepository repo
                = new InMemoryCategoriesRepository(Arrays.asList(monthlyPlansModel));

        List<CategoryModel> all = repo.findAll();
        assertEquals(3, all.size());

        // find last element with frequency period = week
        CategoryModel filtered = repo.findLast(new Predicate<CategoryModel>() {
            @Override
            public boolean apply(CategoryModel categoryModel) {
                return categoryModel.getPeriod() == FrequencyPeriod.Week;
            }
        });

        assertEquals(cm3, filtered);

        // find last category with 'do' in name
        filtered = repo.findLast(new Predicate<CategoryModel>() {
            @Override
            public boolean apply(CategoryModel categoryModel) {
                return categoryModel.getName().toLowerCase().contains("do");
            }
        });

        assertEquals(cm3, filtered);

        // find last category with 'category' in name
        filtered = repo.findLast(new Predicate<CategoryModel>() {
            @Override
            public boolean apply(CategoryModel categoryModel) {
                return categoryModel.getName().contains("Category");
            }
        });

        assertEquals(cm2, filtered);


        // find last category with freq value '2'
        filtered = repo.findLast(new Predicate<CategoryModel>() {
            @Override
            public boolean apply(CategoryModel categoryModel) {
                return categoryModel.getFrequencyValue() == 2;
            }
        });

        assertEquals(cm1, filtered);
    }

    @Test
    public void inMemoryCategoriesRepository_findForMonth_test() throws Exception {
        MonthlyPlansModel april = this.createMonthlyPlans(1, 2016, Month.APRIL, "A1", "A2", "A3", "A4", "A5", "A6");
        MonthlyPlansModel may = this.createMonthlyPlans(2, 2016, Month.MAY, "B1", "B2", "B3", "B4", "B5");

        // setFrequencyPerion repository
        CategoriesRepository repository = new InMemoryCategoriesRepository(CollectionUtils.createList(april, may));

        List<CategoryModel> categories = repository.findForMonth(2016, Month.APRIL);
        assertEquals(6, categories.size());
        assertEquals("A1", categories.get(0).getName());
        assertEquals("A2", categories.get(1).getName());
        assertEquals("A3", categories.get(2).getName());
        assertEquals("A4", categories.get(3).getName());
        assertEquals("A5", categories.get(4).getName());
        assertEquals("A6", categories.get(5).getName());

        categories = repository.findForMonth(2016, Month.MAY);
        assertEquals(5, categories.size());
        assertEquals("B1", categories.get(0).getName());
        assertEquals("B2", categories.get(1).getName());
        assertEquals("B3", categories.get(2).getName());
        assertEquals("B4", categories.get(3).getName());
        assertEquals("B5", categories.get(4).getName());

        categories = repository.findForMonth(2016, Month.JUNE);
        assertEquals(0, categories.size());

        categories = repository.findForMonth(2016, Month.MARCH);
        assertEquals(0, categories.size());
    }


    @Test
    public void inMemoryCategoriesRepository_findForMonthWithName_test() throws Exception {
        MonthlyPlansModel april = this.createMonthlyPlans(1, 2016, Month.APRIL, "A1", "A2", "A3", "A4", "A5", "A6");
        MonthlyPlansModel may = this.createMonthlyPlans(2, 2016, Month.MAY, "B1", "B2", "B3", "B2", "B5");

        // setFrequencyPerion repository
        CategoriesRepository repository = new InMemoryCategoriesRepository(CollectionUtils.createList(april, may));

        List<CategoryModel> categories = repository.findForMonthWithName(2016, Month.APRIL, "A1");
        assertEquals(1, categories.size());
        assertEquals("A1", categories.get(0).getName());

        categories = repository.findForMonthWithName(2016, Month.APRIL, "A8");
        assertEquals(0, categories.size());

        categories = repository.findForMonthWithName(2016, Month.MAY, "B1");
        assertEquals(1, categories.size());
        assertEquals("B1", categories.get(0).getName());

        categories = repository.findForMonthWithName(2016, Month.MAY, "B2");
        assertEquals(2, categories.size());
        assertEquals("B2", categories.get(0).getName());
        assertEquals("B2", categories.get(1).getName());
    }

    @Test
    public void inMemoryCategoriesRepository_findForDateWithStatus_test() throws Exception {
        MonthlyPlansModel april = this.createMonthlyPlans(1, 2016, Month.APRIL, "A1", "A2", "A3", "A4", "A5", "A6");
        MonthlyPlansModel may = this.createMonthlyPlans(2, 2016, Month.MAY, "B1", "B2", "B3", "B4", "B5");

        CategoriesRepository repo = new InMemoryCategoriesRepository(CollectionUtils.createList(april, may));
        // setFrequencyPerion daily plans statuses
        int year = 2016;
        Month month = Month.APRIL;
        int day = 21;
        PlanStatus status = PlanStatus.Planned;
        april.getCategories().get(0).getDailyPlans().get(day-1).setStatus(status); // A1
        april.getCategories().get(2).getDailyPlans().get(day-1).setStatus(status); // A3
        april.getCategories().get(4).getDailyPlans().get(day-1).setStatus(status); // A5


        // find
        List<CategoryModel> actual = repo.findForDateWithStatus(year, month, day, PlanStatus.Success);
        assertEquals(0, actual.size());

        actual = repo.findForDateWithStatus(year, month, day, status);
        assertEquals(3, actual.size());
        assertEquals("A1", actual.get(0).getName());
        assertEquals("A3", actual.get(1).getName());
        assertEquals("A5", actual.get(2).getName());

        actual = repo.findForDateWithStatus(year, month, day, PlanStatus.Empty);
        assertEquals(3, actual.size());
        assertEquals("A2", actual.get(0).getName());
        assertEquals("A4", actual.get(1).getName());
        assertEquals("A6", actual.get(2).getName());

        // mark other
        month = Month.MAY;
        day = 1;
        may.getCategories().get(0).getDailyPlans().get(day-1).setStatus(PlanStatus.Failure);
        may.getCategories().get(1).getDailyPlans().get(day-1).setStatus(PlanStatus.Success);
        may.getCategories().get(2).getDailyPlans().get(day-1).setStatus(PlanStatus.Empty);
        may.getCategories().get(3).getDailyPlans().get(day-1).setStatus(PlanStatus.Planned);
        may.getCategories().get(4).getDailyPlans().get(day-1).setStatus(PlanStatus.Failure);

        actual = repo.findForDateWithStatus(year, month, day, PlanStatus.Planned);
        assertEquals(1, actual.size());
        assertEquals("B4", actual.get(0).getName());

        actual = repo.findForDateWithStatus(year, month, day, PlanStatus.Failure);
        assertEquals(2, actual.size());
        assertEquals("B1", actual.get(0).getName());
        assertEquals("B5", actual.get(1).getName());

        actual = repo.findForDateWithStatus(year, month, day, PlanStatus.Success);
        assertEquals(1, actual.size());
        assertEquals("B2", actual.get(0).getName());

        actual = repo.findForDateWithStatus(year, month, day, PlanStatus.Empty);
        assertEquals(1, actual.size());
        assertEquals("B3", actual.get(0).getName());

        // month not in db
        actual = repo.findForDateWithStatus(year, Month.MARCH, day, PlanStatus.Success);
        assertEquals(0, actual.size());
    }


    private MonthlyPlansModel createMonthlyPlans(int id, int year, Month month, String... categoriesNames)
    {
        MonthlyPlansModel vm = new MonthlyPlansModel(id, year, month);
        for (String catName : categoriesNames) {
            int catId = id^year^month.getNumValue();
            CategoryModel c = new CategoryModel(catId, catName, FrequencyPeriod.Month, 2);
            c.setDailyPlans(ModelHelper.createListOfDailyPlansForMonth(year, month));
            vm.getCategories().add(c);
        }

        return vm;
    }

}