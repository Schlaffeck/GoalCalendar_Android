package com.slamcode.goalcalendar.view.charts.data.hellocharts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.provider.PieChartDataProvider;
import lecho.lib.hellocharts.renderer.PieChartRenderer;
import lecho.lib.hellocharts.view.Chart;

/**
 * Created by smoriak on 24/05/2017.
 */

public class ProgressPieChartRenderer extends PieChartRenderer {

    private PieChartDataProvider dataProvider;
    private boolean slicesToggleEnabled;
    private SelectedValue lastSetValue = new SelectedValue();
    private PointF lastTouchCoordinates = new PointF();

    public ProgressPieChartRenderer(Context context, Chart chart, PieChartDataProvider dataProvider) {
        super(context, chart, dataProvider);
        this.dataProvider = dataProvider;
    }

    @Override
    public void onChartSizeChanged() {
        super.onChartSizeChanged();
        this.calculateProgressSliceCircleOval(null);
    }

    @Override
    public void setCircleFillRatio(float fillRatio) {
        super.setCircleFillRatio(fillRatio);
        this.calculateProgressSliceCircleOval(null);
    }

    @Override
    public boolean checkTouch(float touchX, float touchY) {
        if(slicesToggleEnabled)
        {
            boolean wasTouched = super.checkTouch(touchX, touchY);
            if(wasTouched && selectedValue.isSet()) {
                if(lastTouchCoordinates.equals(touchX, touchY))
                {
                    //lastTouchCoordinates = new PointF();
                }
                else {
                    this.lastTouchCoordinates = new PointF(touchX, touchY);
                    if (selectedValue.getFirstIndex() == this.lastSetValue.getFirstIndex()) {
                        this.lastSetValue.clear();
                        this.selectedValue.clear();
                    }
                    else
                        this.lastSetValue.set(selectedValue);
                }
            }
            return wasTouched;
        }

        return super.checkTouch(touchX, touchY);
    }

    @Override
    public void draw(Canvas canvas) {
        if(slicesToggleEnabled
                && !selectedValue.isSet())
            selectedValue.set(lastSetValue);
        super.draw(canvas);
        // within currently drawn slices set the progress slices
        this.drawProgressOnSlices(canvas);
    }

    private void drawProgressOnSlices(Canvas canvas) {
        final PieChartData data = dataProvider.getPieChartData();
        final float sliceScale = 360f / this.calculateSlicesSum();
        float lastAngle = getChartRotation();
        int sliceIndex = 0;
        for (SliceValue sliceValue : data.getValues()) {
            final float angle = Math.abs(sliceValue.getValue()) * sliceScale;
            if(sliceValue instanceof ProgressSliceValue) {
                this.drawProgressOnSlice(canvas, (ProgressSliceValue) sliceValue, sliceIndex, lastAngle, angle);
            }
            lastAngle += angle;
            sliceIndex++;
        }
        super.drawLabels(canvas);
    }

    private void drawProgressOnSlice(Canvas canvas, ProgressSliceValue sliceValue, int sliceIndex, float lastAngle, float angle) {

        RectF drawCircleOval = calculateProgressSliceCircleOval(sliceValue);
        Paint slicePaint = new Paint();
        if(isTouched() && selectedValue.getFirstIndex() == sliceIndex)
        {
            slicePaint.setColor(sliceValue.getDarkenColor());
        }
        else {
            slicePaint.setColor(this.getLightenedColor(sliceValue.getColor()));
        }
        canvas.drawArc(drawCircleOval, lastAngle, angle, true, slicePaint);
    }

    private int getLightenedColor(int color) {
        float[] hsv = new float[3];
        int alpha = Color.alpha(color);
        Color.colorToHSV(color, hsv);

        final float LIGHTEN_SATURATION = 0.9f;
        final float LIGHTEN_INTENSITY = 1.1f;
        hsv[1] = Math.min(hsv[1] * LIGHTEN_SATURATION, 1.0f);
        hsv[2] = hsv[2] * LIGHTEN_INTENSITY;
        int tempColor = Color.HSVToColor(hsv);
        return Color.argb(alpha, Color.red(tempColor), Color.green(tempColor), Color.blue(tempColor));
    }

    private RectF calculateProgressSliceCircleOval(ProgressSliceValue sliceValue)
    {
        Rect contentRect = computator.getContentRectMinusAllMargins();

        float progressValue = sliceValue != null ? 1.0f * sliceValue.getProgressValue() / sliceValue.getThresholdValue() : 1f;
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

    public boolean isSlicesToggleEnabled() {
        return slicesToggleEnabled;
    }

    public void setSlicesToggleEnabled(boolean slicesToggleEnabled) {
        this.slicesToggleEnabled = slicesToggleEnabled;
    }
}
