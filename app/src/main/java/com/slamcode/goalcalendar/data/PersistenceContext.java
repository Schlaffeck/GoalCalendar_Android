package com.slamcode.goalcalendar.data;

/**
 * Created by moriasla on 03.01.2017.
 */

public interface PersistenceContext {

    void persistData();

    void initializePersistedData();

    UnitOfWork createUnitOfWork();

    void addContextChangedListener(PersistenceContextChangedListener listener);

    void removeContextChangedListener(PersistenceContextChangedListener listener);

    void clearContextChangedListeners();

    interface PersistenceContextChangedListener
    {
        void onContextPersisted();
    }
}
