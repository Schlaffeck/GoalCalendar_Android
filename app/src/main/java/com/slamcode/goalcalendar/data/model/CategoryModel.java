package com.slamcode.goalcalendar.data.model;

import com.slamcode.goalcalendar.data.Identifiable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public class CategoryModel implements Identifiable<Integer> {

    private int id;

    private String name;

    private FrequencyModel frequency;

    private List<DailyPlanModel> dailyPlans = new ArrayList<DailyPlanModel>();

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

    public FrequencyModel getFrequency() {
        return frequency;
    }

    public void setFrequency(FrequencyModel frequency) {
        this.frequency = frequency;
    }

    public List<DailyPlanModel> getDailyPlans() {
        return dailyPlans;
    }

    public void setDailyPlans(List<DailyPlanModel> dailyPlans) {
        this.dailyPlans = dailyPlans;
    }
}
