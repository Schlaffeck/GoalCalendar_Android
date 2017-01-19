package com.slamcode.goalcalendar.data.inmemory;

import com.android.internal.util.Predicate;
import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementSelector;
import com.slamcode.goalcalendar.data.*;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import org.apache.commons.collections4.IterableUtils;

import java.util.Collection;
import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public class InMemoryCategoriesRepository extends InMemoryRepositoryBase<CategoryModel, Integer> implements CategoryRepository {

    private List<MonthlyPlansModel> monthlyPlansModels;

    public InMemoryCategoriesRepository(List<MonthlyPlansModel> monthlyPlansModels) {
        super(CollectionUtils.merge(monthlyPlansModels, new ElementSelector<MonthlyPlansModel, Collection<CategoryModel>>() {
            @Override
            public Collection<CategoryModel> select(MonthlyPlansModel parent) {
                return parent.getCategories();
            }
        }));
        this.monthlyPlansModels = monthlyPlansModels;
    }

    public InMemoryCategoriesRepository(){
        super();
    }

    @Override
    public List<CategoryModel> findForMonth(final int year, final Month month) {

        MonthlyPlansModel monthlyPlansModel = IterableUtils.find(this.monthlyPlansModels, new org.apache.commons.collections4.Predicate<MonthlyPlansModel>() {
            @Override
            public boolean evaluate(MonthlyPlansModel object) {

                if(object == null)
                    return false;

                return object.getYear() == year
                        && object.getMonth() == month;
            }
        });
        return monthlyPlansModel != null ? monthlyPlansModel.getCategories() : CollectionUtils.<CategoryModel>emptyList();
    }

    @Override
    public List<CategoryModel> findForDateWithStatus(int year, Month month, int day, final PlanStatus planStatus) {
        Iterable<CategoryModel> result = this.findForMonth(year, month);

        result = IterableUtils.filteredIterable(result, new org.apache.commons.collections4.Predicate<CategoryModel>() {
            @Override
            public boolean evaluate(CategoryModel categoryModel) {
                if (categoryModel == null)
                    return false;

                if (categoryModel.getDailyPlans() == null
                        || categoryModel.getDailyPlans().isEmpty())
                    return false;

                final DailyPlanModel dailyPlanModel = IterableUtils.find(
                        categoryModel.getDailyPlans(),
                        new org.apache.commons.collections4.Predicate<DailyPlanModel>() {
                            @Override
                            public boolean evaluate(DailyPlanModel dailyPlanModel) {
                                return dailyPlanModel != null
                                        && dailyPlanModel.getDayNumber() == DateTimeHelper.currentDayNumber()
                                        && dailyPlanModel.getStatus() == planStatus;
                            }
                        });

                return dailyPlanModel != null;
            }
        });

        return IterableUtils.toList(result);
    }
}
