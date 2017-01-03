package com.slamcode.goalcalendar.data.inmemory;

import com.android.internal.util.Predicate;
import com.slamcode.goalcalendar.data.CategoryRepository;
import com.slamcode.goalcalendar.data.model.CategoryModel;

import com.slamcode.collections.*;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;

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
        CategoryRepository repo = new InMemoryCategoriesRepository();

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
        CategoryRepository repo
                = new InMemoryCategoriesRepository(Arrays.asList(cm1, cm2, cm3));

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
        CategoryRepository repo
                = new InMemoryCategoriesRepository(
                CollectionUtils.createList(cm1, cm2, cm3));

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
        FrequencyModel fm1 = new FrequencyModel();
        fm1.setFrequencyValue(2);
        fm1.setPeriod(FrequencyPeriod.Week);
        cm1.setFrequency(fm1);
        cm1.setName("Category 1 - XYZ");

        CategoryModel cm2 = new CategoryModel(2);
        FrequencyModel fm2 = new FrequencyModel();
        fm2.setFrequencyValue(3);
        fm2.setPeriod(FrequencyPeriod.Month);
        cm2.setFrequency(fm2);
        cm2.setName("Category 2 - do stuff");

        CategoryModel cm3 = new CategoryModel(3);
        FrequencyModel fm3 = new FrequencyModel();
        fm3.setFrequencyValue(1);
        fm3.setPeriod(FrequencyPeriod.Week);
        cm3.setFrequency(fm3);
        cm3.setName("Cat 3 - Do more");
        cm3.setDailyPlans(CollectionUtils.createList(new DailyPlanModel()));

        CategoryRepository repo
                = new InMemoryCategoriesRepository(
                CollectionUtils.createList(cm1, cm2, cm3));

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
                return categoryModel.getFrequency().getPeriod() == FrequencyPeriod.Week;
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
        FrequencyModel fm1 = new FrequencyModel();
        fm1.setFrequencyValue(2);
        fm1.setPeriod(FrequencyPeriod.Week);
        cm1.setFrequency(fm1);
        cm1.setName("Category 1 - XYZ");

        CategoryModel cm2 = new CategoryModel(2);
        FrequencyModel fm2 = new FrequencyModel();
        fm2.setFrequencyValue(3);
        fm2.setPeriod(FrequencyPeriod.Month);
        cm2.setFrequency(fm2);
        cm2.setName("Category 2 - do stuff");

        CategoryModel cm3 = new CategoryModel(3);
        FrequencyModel fm3 = new FrequencyModel();
        fm3.setFrequencyValue(1);
        fm3.setPeriod(FrequencyPeriod.Week);
        cm3.setFrequency(fm3);
        cm3.setName("Cat 3 - Do more");
        cm3.setDailyPlans(CollectionUtils.createList(new DailyPlanModel()));

        CategoryRepository repo
                = new InMemoryCategoriesRepository(
                CollectionUtils.createList(cm1, cm2, cm3));

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
                return categoryModel.getFrequency().getFrequencyValue() >= 2;
            }
        });

        assertEquals(cm1, filtered);

        // find first with given frequency value
        filtered = repo.findFirst(new Predicate<CategoryModel>() {
            @Override
            public boolean apply(CategoryModel categoryModel) {
                return categoryModel.getFrequency().getPeriod() == FrequencyPeriod.Month;
            }
        });

        assertEquals(cm2, filtered);
    }
}