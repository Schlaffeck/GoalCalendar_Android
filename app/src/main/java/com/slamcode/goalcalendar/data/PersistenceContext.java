package com.slamcode.goalcalendar.data;

import com.slamcode.goalcalendar.data.model.plans.MonthlyPlansDataBundle;
import com.slamcode.goalcalendar.data.unitofwork.UnitOfWork;

/**
 * Created by moriasla on 03.01.2017.
 */

public interface PersistenceContext {

    void persistData();

    void initializePersistedData();

    // TODO: Refactor method to not be public somehow
    MonthlyPlansDataBundle getDataBundle();

    // TODO: Refactor method to not be public somehow
    void setDataBundle(MonthlyPlansDataBundle dataBundle);

    /**
     * Creates unit of work persistable by default
     * @return Persistable non-readonly unit of work
     */
    UnitOfWork createUnitOfWork();

    /**
     * Creates new unit of work wither readonly or not
     * @param readonly Flag indicating whether unit of work should be readonly non-persistable
     * @return Unot of work
     */
    UnitOfWork createUnitOfWork(boolean readonly);

    void addContextChangedListener(PersistenceContextChangedListener listener);

    void removeContextChangedListener(PersistenceContextChangedListener listener);

    void clearContextChangedListeners();

    interface PersistenceContextChangedListener
    {
        void onContextPersisted();
    }
}
