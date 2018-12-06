package com.slamcode.goalcalendar.data;

import com.android.util.Predicate;

import java.util.List;

/**
 * Created by moriasla on 16.12.2016.
 */

public interface Repository<TEntity extends Identifiable<TId>, TId extends Comparable<TId>> {

    TEntity findById(TId id);

    List<TEntity> findMany(Predicate<TEntity> predicate);

    TEntity findFirst(Predicate<TEntity> predicate);

    TEntity findLast(Predicate<TEntity> predicate);

    List<TEntity> findAll();

    void remove(TEntity entity);

    void add(TEntity entity);
}
