package com.slamcode.goalcalendar.planning;


import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
/**
 * Created by moriasla on 02.01.2017.
 */

public class MonthTest {

    @Test
    public void month_GetNumValue_Test()
    {
        Assert.assertEquals(1, Month.JANUARY.getNumValue());
        Assert.assertEquals(2, Month.FEBRUARY.getNumValue());
        Assert.assertEquals(3, Month.MARCH.getNumValue());
        Assert.assertEquals(4, Month.APRIL.getNumValue());
        Assert.assertEquals(5, Month.MAY.getNumValue());
        Assert.assertEquals(6, Month.JUNE.getNumValue());
        Assert.assertEquals(7, Month.JULY.getNumValue());
        Assert.assertEquals(8, Month.AUGUST.getNumValue());
        Assert.assertEquals(9, Month.SEPTEMBER.getNumValue());
        Assert.assertEquals(10, Month.OCTOBER.getNumValue());
        Assert.assertEquals(11, Month.NOVEMBER.getNumValue());
        Assert.assertEquals(12, Month.DECEMBER.getNumValue());
    }

    @Test
    public void month_GetCurrentMonth_Test()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Assert.assertEquals(cal.get(Calendar.MONTH) + 1, Month.getCurrentMonth().getNumValue());
    }

    @Test
    public void month_GetPreviousMonth_Test()
    {
        Month m = Month.JANUARY;
        Month nextM = Month.getPreviousMonth(m);
        Assert.assertEquals(Month.DECEMBER, nextM);

        m = Month.FEBRUARY;
        nextM = Month.getPreviousMonth(m);
        Assert.assertEquals(Month.JANUARY, nextM);

        m = Month.MARCH;
        nextM = Month.getPreviousMonth(m);
        Assert.assertEquals(Month.FEBRUARY, nextM);

        m = Month.APRIL;
        nextM = Month.getPreviousMonth(m);
        Assert.assertEquals(Month.MARCH, nextM);

        m = Month.MAY;
        nextM = Month.getPreviousMonth(m);
        Assert.assertEquals(Month.APRIL, nextM);

        m = Month.JUNE;
        nextM = Month.getPreviousMonth(m);
        Assert.assertEquals(Month.MAY, nextM);

        m = Month.JULY;
        nextM = Month.getPreviousMonth(m);
        Assert.assertEquals(Month.JUNE, nextM);

        m = Month.AUGUST;
        nextM = Month.getPreviousMonth(m);
        Assert.assertEquals(Month.JULY, nextM);

        m = Month.SEPTEMBER;
        nextM = Month.getPreviousMonth(m);
        Assert.assertEquals(Month.AUGUST, nextM);

        m = Month.OCTOBER;
        nextM = Month.getPreviousMonth(m);
        Assert.assertEquals(Month.SEPTEMBER, nextM);

        m = Month.NOVEMBER;
        nextM = Month.getPreviousMonth(m);
        Assert.assertEquals(Month.OCTOBER, nextM);

        m = Month.DECEMBER;
        nextM = Month.getPreviousMonth(m);
        Assert.assertEquals(Month.NOVEMBER, nextM);
    }

    @Test
    public void month_GetNextMonth_Test()
    {
        Month m = Month.JANUARY;
        Month nextM = Month.getNextMonth(m);
        Assert.assertEquals(Month.FEBRUARY, nextM);

        m = Month.FEBRUARY;
        nextM = Month.getNextMonth(m);
        Assert.assertEquals(Month.MARCH, nextM);

        m = Month.MARCH;
        nextM = Month.getNextMonth(m);
        Assert.assertEquals(Month.APRIL, nextM);

        m = Month.APRIL;
        nextM = Month.getNextMonth(m);
        Assert.assertEquals(Month.MAY, nextM);

        m = Month.MAY;
        nextM = Month.getNextMonth(m);
        Assert.assertEquals(Month.JUNE, nextM);

        m = Month.JUNE;
        nextM = Month.getNextMonth(m);
        Assert.assertEquals(Month.JULY, nextM);

        m = Month.JULY;
        nextM = Month.getNextMonth(m);
        Assert.assertEquals(Month.AUGUST, nextM);

        m = Month.AUGUST;
        nextM = Month.getNextMonth(m);
        Assert.assertEquals(Month.SEPTEMBER, nextM);

        m = Month.SEPTEMBER;
        nextM = Month.getNextMonth(m);
        Assert.assertEquals(Month.OCTOBER, nextM);

        m = Month.OCTOBER;
        nextM = Month.getNextMonth(m);
        Assert.assertEquals(Month.NOVEMBER, nextM);

        m = Month.NOVEMBER;
        nextM = Month.getNextMonth(m);
        Assert.assertEquals(Month.DECEMBER, nextM);

        m = Month.DECEMBER;
        nextM = Month.getNextMonth(m);
        Assert.assertEquals(Month.JANUARY, nextM);
    }

    @Test
    public void month_getDaysCount_test()
    {
        int year = 2017;
        int[] expectedNoOfDays = new int[]
                {
                  31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
                };

        int i = 0;
        for (Month m : Month.values()) {
            Assert.assertEquals("For month: " + m, expectedNoOfDays[i], m.getDaysCount(year));
            i++;
        }
    }

    @Test
    public void month_getDaysCount_leapYear_test()
    {
        int year = 2016;
        int[] expectedNoOfDays = new int[]
                {
                        31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
                };

        int i = 0;
        for (Month m : Month.values()) {
            Assert.assertEquals("For month: " + m, expectedNoOfDays[i], m.getDaysCount(year));
            i++;
        }
    }
}
