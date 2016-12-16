package com.slamcode.goalcalendar.data;

import com.slamcode.goalcalendar.data.model.CategoryModel;
import com.slamcode.goalcalendar.planning.Month;

import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public interface CategoryRepository extends Repository<CategoryModel,  Integer> {

    List<CategoryModel> findForMonth(Month month);
}
