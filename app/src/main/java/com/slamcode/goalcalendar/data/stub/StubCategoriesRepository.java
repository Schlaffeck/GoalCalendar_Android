package com.slamcode.goalcalendar.data.stub;

import com.android.internal.util.Predicate;
import com.slamcode.goalcalendar.data.*;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.FrequencyModel;
import com.slamcode.goalcalendar.data.model.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public class StubCategoriesRepository implements CategoryRepository {

    List<CategoryModel> stubList = new ArrayList<CategoryModel>();

    public StubCategoriesRepository(List<CategoryModel> list)
    {
        this.stubList = list;
    }

    @Override
    public CategoryModel findById(Integer comparable) {
        for (CategoryModel cm : this.stubList) {
            if (cm.getId().equals(comparable))
            {
                return cm;
            }
        }
        return null;
    }

    @Override
    public CategoryModel findFirst(Predicate<CategoryModel> predicate) {
        for (CategoryModel cm : this.stubList) {
            if (predicate.apply(cm))
            {
                return cm;
            }
        }
        return null;
    }

    @Override
    public List<CategoryModel> findAll() {
        return this.stubList;
    }

    @Override
    public List<CategoryModel> findMany(Predicate<CategoryModel> predicate) {

        List<CategoryModel> resultList = new ArrayList<CategoryModel>();

        for (CategoryModel cm : this.stubList) {
            if (predicate.apply(cm))
            {
                resultList.add(cm);
            }
        }
        return resultList;
    }

    public static StubCategoriesRepository buildDefaultRepository()
    {
        FrequencyModel freq1 =  new FrequencyModel();
        freq1.setId(1);
        freq1.setPeriod(FrequencyPeriod.Week);
        freq1.setFrequencyValue(2);

        CategoryModel category1 = new CategoryModel();
        category1.setId(1);
        category1.setName("Family");
        category1.setFrequency(freq1);
        category1.setDailyPlans(Arrays.asList(new DailyPlanModel(), new DailyPlanModel(), new DailyPlanModel()));

        FrequencyModel freq2 =  new FrequencyModel();
        freq2.setId(2);
        freq2.setPeriod(FrequencyPeriod.Month);
        freq2.setFrequencyValue(3);

        CategoryModel category2 = new CategoryModel();
        category2.setId(2);
        category2.setName("Project");
        category2.setFrequency(freq2);
        category2.setDailyPlans(Arrays.asList(new DailyPlanModel(), new DailyPlanModel(), new DailyPlanModel()));

        StubCategoriesRepository repository = new StubCategoriesRepository(
                Arrays.asList(category1, category2)
        );

        return repository;
    }

    @Override
    public List<CategoryModel> findForMonth(Month month) {
        return this.stubList;
    }
}
