package com.slamcode.goalcalendar.planning.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Shared Registry of all date time change listeners
 */

public class DateTimeChangeListenersRegistry {

    private List<DateTimeChangeListener> listenersList = new ArrayList<>();

    public Collection<DateTimeChangeListener> getListeners()
    {
        return listenersList;
    }

    public void registerListener(DateTimeChangeListener listener)
    {
        this.listenersList.add(listener);
    }

    public void unregisterListener(DateTimeChangeListener listener)
    {
        this.listenersList.remove(listener);
    }

}
