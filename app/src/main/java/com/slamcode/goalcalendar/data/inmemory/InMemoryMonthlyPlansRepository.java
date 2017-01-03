package com.slamcode.goalcalendar.data.inmemory;

import com.android.internal.util.Predicate;
import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementCreator;
import com.slamcode.goalcalendar.data.MonthlyPlansRepository;
import com.slamcode.goalcalendar.data.Repository;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.Month;

import java.util.List;

/**
 * Created by moriasla on 23.12.2016.
 */

public class InMemoryMonthlyPlansRepository extends InMemoryRepositoryBase<MonthlyPlansModel,Integer> implements MonthlyPlansRepository {

    public InMemoryMonthlyPlansRepository(List<MonthlyPlansModel> entities)
    {
        super(entities);
    }

    @Override
    public MonthlyPlansModel findForMonth(Month month) {
        for(MonthlyPlansModel m : this.getInMemoryEntityList())
        {
            if(month.equals(m.getMonth()))
            {
                return m;
            }
        }

        return null;
    }

    @Override
    public MonthlyPlansModel findForThisMonth() {
        return this.findForMonth(Month.getCurrentMonth());
    }

    @Override
    public MonthlyPlansModel findForNextMonth() {
        return this.findForMonth(Month.getNextMonth());
    }

    @Override
    public MonthlyPlansModel findForPreviousMonth() {
        return this.findForMonth(Month.getPreviousMonth());
    }

    public static InMemoryMonthlyPlansRepository buildDefaultRepository()
    {
        List<MonthlyPlansModel> entities = CollectionUtils.createList(Month.values().length, new ElementCreator<MonthlyPlansModel>() {
            @Override
            public MonthlyPlansModel Create(int index, List<MonthlyPlansModel> currentList) {
                MonthlyPlansModel plans = new MonthlyPlansModel();
                plans.setId(index+1);
                plans.setMonth(Month.getMonthByNumber(index + 1));
                plans.setCategories(InMemoryCategoriesRepository.buildCategoriesList((
                        index+1)*Month.values().length, plans.getMonth()));
                return plans;
            }
        });
        return new InMemoryMonthlyPlansRepository(entities);
    }
}
