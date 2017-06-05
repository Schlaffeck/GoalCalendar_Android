package com.slamcode.goalcalendar.data.inmemory;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementSelector;
import com.slamcode.goalcalendar.data.*;
import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.ModelHelper;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.data.query.NumericalComparisonOperator;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

import java.util.Collection;
import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public class InMemoryCategoriesRepository extends InMemoryRepositoryBase<CategoryModel, Integer> implements CategoriesRepository {

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
    public  List<CategoryModel> findForMonthWithName(int year, Month month, final String name) {
        Iterable<CategoryModel> result = this.findForMonth(year, month);

        result = IterableUtils.filteredIterable(result, new Predicate<CategoryModel>() {
            @Override
            public boolean evaluate(CategoryModel object) {
                return object != null && object.getName() == name;
            }
        });

        return IterableUtils.toList(result);
    }

    @Override
    public List<CategoryModel> findForDateWithStatus(int year, Month month, int day, PlanStatus planStatus) {
        Iterable<CategoryModel> result = this.findForMonth(year, month);

        result = IterableUtils.filteredIterable(result, ModelHelper.getCategoryOfStatusOnDayPredicate(planStatus, day));

        return IterableUtils.toList(result);
    }

    @Override
    public List<CategoryModel> findNotDoneInMonth(int year, Month month) {
        return this.findWithProgressInMonth(year, month, NumericalComparisonOperator.EQUAL_TO, 0);
    }

    @Override
    public List<CategoryModel> findWithProgressInMonth(int year, Month month, NumericalComparisonOperator operator, float progressValue) {
        Iterable<CategoryModel> result = this.findForMonth(year, month);

        result = IterableUtils.filteredIterable(result, ModelHelper.getCategoryOfProgressPredicate(operator, progressValue));

        return IterableUtils.toList(result);
    }
}
