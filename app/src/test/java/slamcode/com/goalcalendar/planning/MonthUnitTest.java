package slamcode.com.goalcalendar.planning;

import com.slamcode.goalcalendar.planning.Month;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
/**
 * Created by moriasla on 02.01.2017.
 */

public class MonthUnitTest {

    @Test
    public void month_GetNumValue_Test()
    {
        assertEquals(1, Month.JANUARY.getNumValue());
        assertEquals(2, Month.FEBRUARY.getNumValue());
        assertEquals(3, Month.MARCH.getNumValue());
        assertEquals(4, Month.APRIL.getNumValue());
        assertEquals(5, Month.MAY.getNumValue());
        assertEquals(6, Month.JUNE.getNumValue());
        assertEquals(7, Month.JULY.getNumValue());
        assertEquals(8, Month.AUGUST.getNumValue());
        assertEquals(9, Month.SEPTEMBER.getNumValue());
        assertEquals(10, Month.OCTOBER.getNumValue());
        assertEquals(11, Month.NOVEMBER.getNumValue());
        assertEquals(12, Month.DECEMBER.getNumValue());
    }

    @Test
    public void month_GetCurrentMonth_Test()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        assertEquals(cal.get(Calendar.MONTH) + 1, Month.getCurrentMonth().getNumValue());
    }

    @Test
    public void month_GetPreviousMonth_Test()
    {
        Month m = Month.JANUARY;
        Month nextM = Month.getPreviousMonth(m);
        assertEquals(Month.DECEMBER, nextM);

        m = Month.FEBRUARY;
        nextM = Month.getPreviousMonth(m);
        assertEquals(Month.JANUARY, nextM);

        m = Month.MARCH;
        nextM = Month.getPreviousMonth(m);
        assertEquals(Month.FEBRUARY, nextM);

        m = Month.APRIL;
        nextM = Month.getPreviousMonth(m);
        assertEquals(Month.MARCH, nextM);

        m = Month.MAY;
        nextM = Month.getPreviousMonth(m);
        assertEquals(Month.APRIL, nextM);

        m = Month.JUNE;
        nextM = Month.getPreviousMonth(m);
        assertEquals(Month.MAY, nextM);

        m = Month.JULY;
        nextM = Month.getPreviousMonth(m);
        assertEquals(Month.JUNE, nextM);

        m = Month.AUGUST;
        nextM = Month.getPreviousMonth(m);
        assertEquals(Month.JULY, nextM);

        m = Month.SEPTEMBER;
        nextM = Month.getPreviousMonth(m);
        assertEquals(Month.AUGUST, nextM);

        m = Month.OCTOBER;
        nextM = Month.getPreviousMonth(m);
        assertEquals(Month.SEPTEMBER, nextM);

        m = Month.NOVEMBER;
        nextM = Month.getPreviousMonth(m);
        assertEquals(Month.OCTOBER, nextM);

        m = Month.DECEMBER;
        nextM = Month.getPreviousMonth(m);
        assertEquals(Month.NOVEMBER, nextM);
    }

    @Test
    public void month_GetNextMonth_Test()
    {
        Month m = Month.JANUARY;
        Month nextM = Month.getNextMonth(m);
        assertEquals(Month.FEBRUARY, nextM);

        m = Month.FEBRUARY;
        nextM = Month.getNextMonth(m);
        assertEquals(Month.MARCH, nextM);

        m = Month.MARCH;
        nextM = Month.getNextMonth(m);
        assertEquals(Month.APRIL, nextM);

        m = Month.APRIL;
        nextM = Month.getNextMonth(m);
        assertEquals(Month.MAY, nextM);

        m = Month.MAY;
        nextM = Month.getNextMonth(m);
        assertEquals(Month.JUNE, nextM);

        m = Month.JUNE;
        nextM = Month.getNextMonth(m);
        assertEquals(Month.JULY, nextM);

        m = Month.JULY;
        nextM = Month.getNextMonth(m);
        assertEquals(Month.AUGUST, nextM);

        m = Month.AUGUST;
        nextM = Month.getNextMonth(m);
        assertEquals(Month.SEPTEMBER, nextM);

        m = Month.SEPTEMBER;
        nextM = Month.getNextMonth(m);
        assertEquals(Month.OCTOBER, nextM);

        m = Month.OCTOBER;
        nextM = Month.getNextMonth(m);
        assertEquals(Month.NOVEMBER, nextM);

        m = Month.NOVEMBER;
        nextM = Month.getNextMonth(m);
        assertEquals(Month.DECEMBER, nextM);

        m = Month.DECEMBER;
        nextM = Month.getNextMonth(m);
        assertEquals(Month.JANUARY, nextM);
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
            assertEquals("For month: " + m, expectedNoOfDays[i], m.getDaysCount(year));
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
            assertEquals("For month: " + m, expectedNoOfDays[i], m.getDaysCount(year));
            i++;
        }
    }
}
