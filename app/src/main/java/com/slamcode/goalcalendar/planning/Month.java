package com.slamcode.goalcalendar.planning;

import java.util.*;

/**
 * Created by moriasla on 16.12.2016.
 */

public enum Month {

    JANUARY(1),
    FEBRUARY(2),
    MARCH(3),
    APRIL(4),
    MAY(5),
    JUNE(6),
    JULY(7),
    AUGUST(8),
    SEPTEMBER(9),
    OCTOBER(10),
    NOVEMBER(11),
    DECEMBER(12);

    private int numValue;

    Month(int numValue)
    {
        this.numValue = numValue;
    }

    public static Month getCurrentMonth()
    {
        Date dateNow = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateNow);
        int month = cal.get(Calendar.MONTH);

        for(Month m : Month.values())
        {
            if(m.numValue == month + 1)
            {
                return m;
            }
        }

        return null;
    }
}
