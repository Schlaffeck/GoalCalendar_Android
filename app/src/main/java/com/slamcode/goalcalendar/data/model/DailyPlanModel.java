package com.slamcode.goalcalendar.data.model;

import com.slamcode.goalcalendar.data.Identifiable;
import com.slamcode.goalcalendar.planning.PlanStatus;

/**
 * Created by moriasla on 16.12.2016.
 */

public class DailyPlanModel implements Identifiable<Integer> {

    private int id;

    private PlanStatus status = PlanStatus.Empty;

    private int dayNumber;

    public DailyPlanModel()
    {

    }

    public DailyPlanModel(PlanStatus status, int dayNumber)
    {
        this.status = status;
        this.dayNumber = dayNumber;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public PlanStatus getStatus() {
        return status;
    }

    public void setStatus(PlanStatus status) {
        this.status = status;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }
}
