package com.slamcode.goalcalendar.data.model;

import com.slamcode.goalcalendar.data.Identifiable;
import com.slamcode.goalcalendar.planning.FrequencyPeriod;

/**
 * Created by moriasla on 16.12.2016.
 */

public class FrequencyModel implements Identifiable<Integer> {

    private int id;

    private FrequencyPeriod period;

    private int frequencyValue;

    @Override
    public Integer getId() {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
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
    public String toString()
    {
        return String.format("%s x %s", this.frequencyValue, this.period);
    }
}
