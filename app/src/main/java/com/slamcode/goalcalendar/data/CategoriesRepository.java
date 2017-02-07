package com.slamcode.goalcalendar.data;

import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.data.model.DailyPlanModel;
import com.slamcode.goalcalendar.planning.Month;
import com.slamcode.goalcalendar.planning.PlanStatus;

import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public interface CategoriesRepository extends Repository<CategoryModel,  Integer> {

    List<CategoryModel> findForMonth(int year, Month month);

    List<CategoryModel> findForDateWithStatus(int year, Month month, int day, PlanStatus planStatus);
}
