package com.slamcode.goalcalendar.data;

/**
 * Created by moriasla on 03.01.2017.
 */

public interface PersistenceContext {

    void persistData();

    void initializePersistedData();

    UnitOfWork createUnitOfWork();
}
