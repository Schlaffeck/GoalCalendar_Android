package com.slamcode.goalcalendar.view.mvvm;

/**
 * Created by moriasla on 13.02.2017.
 */

public interface PropertyObservable {

    void propertyChanged(String propertyName);

    void addPropertyObserver(PropertyObserver obserer);

    void removePropertyObserver(PropertyObserver obserer);
}
