package com.slamcode.goalcalendar.view.mvvm;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by moriasla on 13.02.2017.
 */

public class PropertyObservableObject implements PropertyObservable {

    private Collection<PropertyObserver> propertyObservers = new ArrayList<>();

    @Override
    public void addPropertyObserver(PropertyObserver obserer) {
        if(obserer == null || propertyObservers.contains(obserer))
            return;
        propertyObservers.add(obserer);
    }

    @Override
    public void removePropertyObserver(PropertyObserver obserer) {
        if(propertyObservers.contains(obserer))
            propertyObservers.remove(obserer);
    }

    @Override
    public void propertyChanged(String propertyName) {
        for (PropertyObserver observer : propertyObservers) {
            if(observer != null)
                observer.onPropertyChanged(propertyName);
        }
    }
}
