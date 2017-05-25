package com.slamcode.goalcalendar.view.charts.data.hellocharts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.provider.PieChartDataProvider;
import lecho.lib.hellocharts.renderer.PieChartRenderer;
import lecho.lib.hellocharts.view.Chart;

/**
 * Created by smoriak on 24/05/2017.
 */

public class ProgressPieChartRenderer extends PieChartRenderer {

    private PieChartDataProvider dataProvider;

    public ProgressPieChartRenderer(Context context, Chart chart, PieChartDataProvider dataProvider) {
        super(context, chart, dataProvider);
        this.dataProvider = dataProvider;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // within currently drawn slices set the progress slices
        this.drawProgressOnSlices(canvas);
    }

    private void drawProgressOnSlices(Canvas canvas) {
        final PieChartData data = dataProvider.getPieChartData();
        final float sliceScale = 360f / this.calculateSlicesSum();
        float lastAngle = getChartRotation();
        for (SliceValue sliceValue : data.getValues()) {
            final float angle = Math.abs(sliceValue.getValue()) * sliceScale;
            if(sliceValue instanceof ProgressSliceValue) {
                this.drawProgressOnSlice(canvas, (ProgressSliceValue) sliceValue, lastAngle, angle);
            }
            lastAngle += angle;
        }
    }

    private void drawProgressOnSlice(Canvas canvas, ProgressSliceValue sliceValue, float lastAngle, float angle) {

        RectF drawCircleOval = calculateProgressSliceCircleOval(sliceValue);
        Paint slicePaint = new Paint();
        slicePaint.setColor(sliceValue.getDarkenColor());
        canvas.drawArc(drawCircleOval, lastAngle, angle, true, slicePaint);
    }

    private RectF calculateProgressSliceCircleOval(ProgressSliceValue sliceValue)
    {
        Rect contentRect = computator.getContentRectMinusAllMargins();

        float progressValue = 1.0f * sliceValue.getProgressValue() / sliceValue.getThresholdValue();
        if(progressValue > 1f)
            progressValue = 1.01f;
        final float circleRadius = Math.min(contentRect.width() / 2f, contentRect.height() / 2f) * progressValue;

        final float centerX = contentRect.centerX();
        final float centerY = contentRect.centerY();
        final float left = centerX - circleRadius;
        final float top = centerY - circleRadius;
        final float right = centerX + circleRadius;
        final float bottom = centerY + circleRadius;

        RectF progressSliceCircleOval = new RectF();
        progressSliceCircleOval.set(left, top, right, bottom);
        final float inset = 0.5f * progressSliceCircleOval.width() * (1.0f - getCircleFillRatio());
        progressSliceCircleOval.inset(inset, inset);

        return progressSliceCircleOval;
    }

    private float calculateSlicesSum()
    {
        float maxSum = 0.0f;
        for (SliceValue sliceValue : dataProvider.getPieChartData().getValues()) {
            maxSum += Math.abs(sliceValue.getValue());
        }

        return maxSum;
    }
}
