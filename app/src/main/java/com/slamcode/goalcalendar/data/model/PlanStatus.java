package com.slamcode.goalcalendar.data.model;

/**
 * Created by moriasla on 16.12.2016.
 */

public enum PlanStatus {

    Empty,
    Planned,
    Success,
    Failure;

    public PlanStatus nextStatus() {
        switch (this)
        {
            case Empty: return Planned;
            case Planned: return Success;
            case Success: return Failure;
            case Failure: return Empty;
        }

        return null;
    }
}
