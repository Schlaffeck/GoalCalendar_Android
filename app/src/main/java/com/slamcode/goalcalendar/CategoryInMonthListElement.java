package com.slamcode.goalcalendar;

/**
 * Created by moriasla on 15.12.2016.
 */

public class CategoryInMonthListElement {

    private final boolean[] dailyPlans;

    private String name;

    private String frequency;

    public CategoryInMonthListElement(String name, String frequency){

        this.frequency = frequency;
        this.name = name;
        this.dailyPlans = new boolean[31];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public boolean[] getDailyPlans() {
        return dailyPlans;
    }
}
