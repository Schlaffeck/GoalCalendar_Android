package com.slamcode.goalcalendar.data.inmemory;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementCreator;
import com.slamcode.goalcalendar.data.*;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public class InMemoryCategoriesRepository extends InMemoryRepositoryBase<CategoryModel, Integer> implements CategoryRepository {


    public InMemoryCategoriesRepository(List<CategoryModel> categories) {
        super(categories);
    }

    public InMemoryCategoriesRepository(){
        super();
    }

    @Override
    public List<CategoryModel> findForMonth(Month month) {
        return this.getInMemoryEntityList();
    }

    static List<CategoryModel> buildCategoriesList(final int startingIndex, final Month month)
    {
        final String[] categoriesNames = new String[]{
                "Family",
                "Friends",
                "Project",
                "Flat arrangement",
                "Create something",
                "Go play some footbal",
                "Exercise",
        };

        final FrequencyPeriod[] categoriesFrequency = new FrequencyPeriod[]
                {
                        FrequencyPeriod.Month,
                        FrequencyPeriod.Week,
                        FrequencyPeriod.Month,
                        FrequencyPeriod.Week,
                        FrequencyPeriod.Week,
                        FrequencyPeriod.Month,
                        FrequencyPeriod.Week,
                };

        final int[] categoriesFrequencyValues = new int[]
                {
                        2,
                        2,
                        3,
                        1,
                        2,
                        3,
                        2
                };

        List<CategoryModel> categories = CollectionUtils.createList(categoriesNames.length, new ElementCreator<CategoryModel>() {
                    @Override
                    public CategoryModel Create(int index, List<CategoryModel> currentList) {
                        CategoryModel result = new CategoryModel(
                                index+startingIndex,
                                categoriesNames[index],
                                categoriesFrequency[index],
                                categoriesFrequencyValues[index]);

                        result.setDailyPlans(CollectionUtils.createList(month.getDaysCount(), new ElementCreator<DailyPlanModel>() {
                            @Override
                            public DailyPlanModel Create(int index, List<DailyPlanModel> currentList) {
                                return new DailyPlanModel(index+1, PlanStatus.Empty, index+1);
                            }
                        }));
                        return result;
                    }
                }
        );

        return categories;
    }

    public static InMemoryCategoriesRepository buildDefaultRepository()
    {
        List<CategoryModel> categories = buildCategoriesList(1, Month.getCurrentMonth());

        InMemoryCategoriesRepository repository = new InMemoryCategoriesRepository(categories);

        return repository;
    }
}
