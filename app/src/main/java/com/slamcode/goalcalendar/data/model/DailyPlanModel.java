package com.slamcode.goalcalendar.data.model;

import com.slamcode.goalcalendar.data.Identifiable;
import com.slamcode.goalcalendar.planning.PlanStatus;

/**
 * Created by moriasla on 16.12.2016.
 */

public class DailyPlanModel implements Identifiable<Integer> {

    private int id;

    private PlanStatus status = PlanStatus.Empty;

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
}
