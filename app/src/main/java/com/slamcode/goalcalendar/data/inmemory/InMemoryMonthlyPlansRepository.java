package com.slamcode.goalcalendar.data.inmemory;

import com.android.internal.util.Predicate;
import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.ElementCreator;
import com.slamcode.goalcalendar.data.MonthlyPlansRepository;
import com.slamcode.goalcalendar.data.Repository;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.data.model.MonthlyPlansModel;
import com.slamcode.goalcalendar.planning.DateTimeHelper;
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
        return this.findForMonth(DateTimeHelper.getCurrentYear(), month);
    }

    @Override
    public MonthlyPlansModel findForMonth(int year, Month month) {
        for(MonthlyPlansModel m : this.getInMemoryEntityList())
        {
            if(m.getYear() == year && month.equals(m.getMonth()))
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
}
