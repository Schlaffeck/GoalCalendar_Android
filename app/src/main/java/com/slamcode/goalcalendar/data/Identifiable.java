package com.slamcode.goalcalendar.data;

/**
 * Created by moriasla on 16.12.2016.
 */

public interface Identifiable<TId extends Comparable<TId>>
{
    TId getId();
}
