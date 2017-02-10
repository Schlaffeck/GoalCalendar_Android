package com.slamcode.goalcalendar.data;

/**
 * Created by moriasla on 10.02.2017.
 */

public interface PropertyChangedNotifier {

    void onPropertyChanged(String propertyName);

    void addPropertyChangedListener(PropertyChangedListener listener);

    void removePropertyChangedListener(PropertyChangedListener listener);

    interface PropertyChangedListener{

        void onPropertyChanged(String propertyName);
    }
}
