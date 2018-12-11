package com.slamcode.goalcalendar.data.model.plans;

import com.slamcode.goalcalendar.data.Identifiable;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by moriasla on 16.12.2016.
 */

public class CategoryModel implements Identifiable<Integer>, Comparable<CategoryModel> {

    private int id;

    private String name;

    private List<DailyPlanModel> dailyPlans = new ArrayList<DailyPlanModel>();

    private FrequencyPeriod period;

    private int frequencyValue;

    public CategoryModel()
    {
    }

    public CategoryModel(int id)
    {
        this.id = id;
    }

    public CategoryModel(int id, String name, FrequencyPeriod period, int frequencyValue)
    {
        this.id = id;
        this.name = name;
        this.period = period;
        this.frequencyValue = frequencyValue;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DailyPlanModel> getDailyPlans() {
        return dailyPlans;
    }

    public void setDailyPlans(List<DailyPlanModel> dailyPlans) {
        this.dailyPlans = dailyPlans;
    }

    public FrequencyPeriod getPeriod() {
        return period;
    }

    public void setPeriod(FrequencyPeriod period) {
        this.period = period;
    }

    public int getFrequencyValue() {
        return frequencyValue;
    }

    public void setFrequencyValue(int frequencyValue) {
        this.frequencyValue = frequencyValue;
    }

    @Override
    public int compareTo(CategoryModel categoryModel) {
        if(categoryModel == null)
            return 1;

        Collator usCollator = Collator.getInstance(Locale.getDefault());
        usCollator.setStrength(Collator.PRIMARY);
        return usCollator.compare(this.getName(), categoryModel.getName());
    }
}
