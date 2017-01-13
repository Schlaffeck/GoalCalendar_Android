package com.slamcode.goalcalendar.view.activity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by moriasla on 13.01.2017.
 */

public class ActivityViewStateProvider {

    Map<String, ActivityViewState> activityViewStateMap =  new HashMap<>();

    public ActivityViewState provideStateForActivity(String activityId)
    {
        if(!this.activityViewStateMap.containsKey(activityId))
        {
            this.activityViewStateMap.put(activityId, new ActivityViewState(activityId));
        }

        return this.activityViewStateMap.get(activityId);
    }
}
