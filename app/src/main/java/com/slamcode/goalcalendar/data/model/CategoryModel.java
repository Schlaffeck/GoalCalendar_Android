package com.slamcode.goalcalendar.data.model;

import com.slamcode.goalcalendar.data.Identifiable;

/**
 * Created by moriasla on 16.12.2016.
 */

public class CategoryModel implements Identifiable<Integer> {

    private int id;

    private String name;

    private FrequencyModel frequency;

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
}
