package com.slamcode.goalcalendar.view;

/**
 * Created by moriasla on 09.03.2017.
 */

public class BaseSourceChangeRequest extends SourceChangeRequestNotifier.SourceChangeRequest {

    private final int requestId;

    public BaseSourceChangeRequest(int requestId)
    {
        this.requestId = requestId;
    }

    @Override
    public int getId() {
        return this.requestId;
    }
}
