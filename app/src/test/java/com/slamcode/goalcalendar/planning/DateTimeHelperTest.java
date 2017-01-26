package com.slamcode.goalcalendar.planning;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by moriasla on 19.01.2017.
 */
public class DateTimeHelperTest {

    @Test
    public void dateTimeHelper_getDiffTimeMillis_test() throws Exception {

        long expectedDiff = 10*1000;
        Calendar from = Calendar.getInstance();
        from.setTimeInMillis(System.currentTimeMillis());
        Calendar to = Calendar.getInstance();
        to.add(Calendar.SECOND, 10);

        assertEquals(expectedDiff, DateTimeHelper.getDiffTimeMillis(from, to));

        expectedDiff = 10*60*1000;
        from = Calendar.getInstance();
        from.setTimeInMillis(System.currentTimeMillis());
        to = Calendar.getInstance();
        to.setTimeInMillis(from.getTimeInMillis() + expectedDiff);

        assertEquals(expectedDiff, DateTimeHelper.getDiffTimeMillis(from, to));

        expectedDiff = 30*60*1000;
        from = Calendar.getInstance();
        from.setTimeInMillis(System.currentTimeMillis());
        to = Calendar.getInstance();
        to.add(Calendar.MINUTE, 30);

        assertEquals(expectedDiff, DateTimeHelper.getDiffTimeMillis(from, to));

        expectedDiff = 2*60*60*1000; // two hours
        from = DateTimeHelper.getTodayCalendar();
        to = DateTimeHelper.getTodayCalendar(2, 0, 0);

        assertEquals(expectedDiff, DateTimeHelper.getDiffTimeMillis(from, to));
    }

    @Test
    public void dateTimeHelper_getTodayCalendar_test() throws Exception {

        Calendar actual = DateTimeHelper.getTodayCalendar();

        assertEquals(Calendar.getInstance().get(Calendar.DATE), actual.get(Calendar.DATE));
        assertEquals(Calendar.getInstance().get(Calendar.MONTH), actual.get(Calendar.MONTH));
        assertEquals(Calendar.getInstance().get(Calendar.YEAR), actual.get(Calendar.YEAR));
        assertEquals(0, actual.get(Calendar.HOUR));
        assertEquals(0, actual.get(Calendar.MONTH));
        assertEquals(0, actual.get(Calendar.SECOND));
        assertEquals(0, actual.get(Calendar.MILLISECOND));
    }
}