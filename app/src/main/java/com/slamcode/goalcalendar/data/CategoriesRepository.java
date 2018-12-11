package com.slamcode.goalcalendar.data;

import com.slamcode.goalcalendar.data.model.plans.CategoryModel;
import com.slamcode.goalcalendar.data.query.NumericalComparisonOperator;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public interface CategoriesRepository extends Repository<CategoryModel,  Integer> {

    List<CategoryModel> findForMonth(int year, Month month);

    List<CategoryModel> findForMonthWithName(int year, Month month, String name);

    List<CategoryModel> findForDateWithStatus(int year, Month month, int day, PlanStatus planStatus);

    List<CategoryModel> findNotDoneInMonth(int year, Month month);

    List<CategoryModel> findWithProgressInMonth(int year, Month month, NumericalComparisonOperator operator, float progressValue);
}
