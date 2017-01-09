package com.slamcode.goalcalendar.data.model;

import com.slamcode.goalcalendar.data.Identifiable;
import com.slamcode.goalcalendar.planning.Month;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moriasla on 23.12.2016.
 */

public class MonthlyPlansModel implements Identifiable<Integer> {

    private int id;

    private int year;

    private Month month;

    private List<CategoryModel> categories;

    public MonthlyPlansModel()
    {
        this.categories = new ArrayList<>();
    }

    public MonthlyPlansModel(int id, int year, Month month)
    {
        this();
        this.id = id;
        this.year = year;
        this.month = month;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<CategoryModel> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryModel> categories) {

        this.categories = categories;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
