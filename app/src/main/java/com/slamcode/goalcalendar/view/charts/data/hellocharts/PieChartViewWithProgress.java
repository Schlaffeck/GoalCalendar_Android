package com.slamcode.goalcalendar.view.charts.data.hellocharts;

import android.content.Context;
import android.util.AttributeSet;

import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by schlaffeck on 25.05.2017.
 */

public class PieChartViewWithProgress extends PieChartView {

    public PieChartViewWithProgress(Context context) {
        this(context, null, 0);
    }

    public PieChartViewWithProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieChartViewWithProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.pieChartRenderer = new ProgressPieChartRenderer(context, this, this);
        setChartRenderer(super.pieChartRenderer);
    }
}
