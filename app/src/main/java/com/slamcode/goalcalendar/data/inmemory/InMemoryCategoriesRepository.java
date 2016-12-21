package com.slamcode.goalcalendar.data.inmemory;

import com.android.internal.util.Predicate;
import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementCreator;
import com.slamcode.goalcalendar.data.*;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.FrequencyModel;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public class InMemoryCategoriesRepository implements CategoryRepository {

    List<CategoryModel> inMemoryEntityList = new ArrayList<CategoryModel>();

    public InMemoryCategoriesRepository(List<CategoryModel> list)
    {
        this.inMemoryEntityList = list;
    }

    @Override
    public CategoryModel findById(Integer comparable) {
        for (CategoryModel cm : this.inMemoryEntityList) {
            if (cm.getId().equals(comparable))
            {
                return cm;
            }
        }
        return null;
    }

    @Override
    public CategoryModel findFirst(Predicate<CategoryModel> predicate) {
        for (CategoryModel cm : this.inMemoryEntityList) {
            if (predicate.apply(cm))
            {
                return cm;
            }
        }
        return null;
    }

    @Override
    public List<CategoryModel> findAll() {
        return this.inMemoryEntityList;
    }

    @Override
    public void remove(CategoryModel categoryModel)
    {
        this.inMemoryEntityList.remove(categoryModel);
    }

    @Override
    public void add(CategoryModel categoryModel)
    {
        this.inMemoryEntityList.add(categoryModel);
    }

    @Override
    public List<CategoryModel> findMany(Predicate<CategoryModel> predicate) {

        List<CategoryModel> resultList = new ArrayList<CategoryModel>();

        for (CategoryModel cm : this.inMemoryEntityList) {
            if (predicate.apply(cm))
            {
                resultList.add(cm);
            }
        }
        return resultList;
    }

    public static InMemoryCategoriesRepository buildDefaultRepository()
    {
        final String[] categoriesNames = new String[]{
                "Family",
                "Friends",
                "Project",
                "Flat arrangement"
        };

        final FrequencyPeriod[] categoriesFrequency = new FrequencyPeriod[]
                {
                        FrequencyPeriod.Month,
                        FrequencyPeriod.Week,
                        FrequencyPeriod.Month,
                        FrequencyPeriod.Week
                };

        final int[] categoriesFrequencyValues = new int[]
                {
                        2,
                        2,
                        3,
                        1
                };

        List<CategoryModel> categories = CollectionUtils.createList(categoriesNames.length, new ElementCreator<CategoryModel>() {
                    @Override
                    public CategoryModel Create(int index, List<CategoryModel> currentList) {
                        CategoryModel result = new CategoryModel();

                        result.setId(index+1);
                        result.setName(categoriesNames[index]);

                        FrequencyModel frequencyModel = new FrequencyModel();
                        frequencyModel.setId(index + 1);
                        frequencyModel.setFrequencyValue(categoriesFrequencyValues[index]);
                        frequencyModel.setPeriod(categoriesFrequency[index]);

                        result.setFrequency(frequencyModel);

                        result.setDailyPlans(CollectionUtils.createList(31, new ElementCreator<DailyPlanModel>() {
                            @Override
                            public DailyPlanModel Create(int index, List<DailyPlanModel> currentList) {
                                return new DailyPlanModel();
                            }
                        }));
                        return result;
                    }
                }
        );

        InMemoryCategoriesRepository repository = new InMemoryCategoriesRepository(categories);

        return repository;
    }

    @Override
    public List<CategoryModel> findForMonth(Month month) {
        return this.inMemoryEntityList;
    }
}
