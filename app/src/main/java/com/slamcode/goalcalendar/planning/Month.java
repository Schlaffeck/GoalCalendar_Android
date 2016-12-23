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

    public static Month getMonthByNumber(int monthNumber) {
        for(Month m : Month.values())
        {
            if(m.numValue == monthNumber)
            {
                return m;
            }
        }

        return null;
    }

    public int getNumValue()
    {
        return this.numValue;
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

    public static Month getNextMonth(Month monthToFindNextFor)
    {
        int month = monthToFindNextFor.numValue;

        for(Month m : Month.values())
        {
            if(m.numValue == (month + 1) % 12)
            {
                return m;
            }
        }

        return null;
    }

    public static Month getNextMonth()
    {
        return getNextMonth(getCurrentMonth());
    }

    public static Month getPreviousMonth(Month monthToFindPreviousFor)
    {
        int month = monthToFindPreviousFor.numValue;

        for(Month m : Month.values())
        {
            if((m.numValue +1) % 12 == month)
            {
                return m;
            }
        }

        return null;
    }

    public static Month getPreviousMonth()
    {
        return getPreviousMonth(getCurrentMonth());
    }
}
