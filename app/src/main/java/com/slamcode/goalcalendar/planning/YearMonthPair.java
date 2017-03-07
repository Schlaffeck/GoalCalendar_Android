package com.slamcode.goalcalendar.planning;

/**
 * Structure with year and month data
 */
public class YearMonthPair {

    private int year;

    private Month month;

    public YearMonthPair()
    {
        this(DateTimeHelper.getCurrentYear(), DateTimeHelper.getCurrentMonth());
    }

    public YearMonthPair(int year, Month month) {
        this.month = month;
        this.year = year;
    }

    public int getYear()
    {
        return this.year;
    }

    public Month getMonth()
    {
        return this.month;
    }

    @Override
    public int hashCode() {
        int prime = 37;
        int result = 11;

        result = prime * result + this.year;
        result = prime * result + this.month.hashCode();

        return result;
    }
}
