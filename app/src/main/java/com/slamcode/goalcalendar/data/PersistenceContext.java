package com.slamcode.goalcalendar.data;

import com.slamcode.goalcalendar.data.json.JsonMonthlyPlansDataBundle;

/**
 * Created by moriasla on 03.01.2017.
 */

public interface PersistenceContext {

    void persistData();

    void initializePersistedData();

    // TODO: Refactor method to not be public somehow
    JsonMonthlyPlansDataBundle getDataBundle();

    // TODO: Refactor method to not be public somehow
    void setDataBundle(JsonMonthlyPlansDataBundle dataBundle);

    UnitOfWork createUnitOfWork();

    void addContextChangedListener(PersistenceContextChangedListener listener);

    void removeContextChangedListener(PersistenceContextChangedListener listener);

    void clearContextChangedListeners();

    interface PersistenceContextChangedListener
    {
        void onContextPersisted();
    }
}
