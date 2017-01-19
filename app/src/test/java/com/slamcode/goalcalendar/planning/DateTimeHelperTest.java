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
    }

}