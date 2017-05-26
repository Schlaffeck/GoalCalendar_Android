package com.slamcode.goalcalendar.view.charts.data.hellocharts;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.renderer.ChartRenderer;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by schlaffeck on 25.05.2017.
 */

public class PieChartViewWithProgress extends PieChartView {

    private int selectedIndex = -1;

    private List<SelectedIndexChangedListener> selectedIndexChangedListeners = new ArrayList<>();

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

    public void setSelectedValueToggleEnabled(boolean selectedValueToggleEnabled)
    {
        if(!(pieChartRenderer instanceof ProgressPieChartRenderer))
            return;

        ((ProgressPieChartRenderer)pieChartRenderer).setSlicesToggleEnabled(selectedValueToggleEnabled);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public boolean isSelectedValueToggleEnabled()
    {
        if(!(pieChartRenderer instanceof ProgressPieChartRenderer))
            return false;

        return((ProgressPieChartRenderer)pieChartRenderer).isSlicesToggleEnabled();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        if(result)
        {
            if(getSelectedValue().isSet())
                this.setSelectedIndex(getSelectedValue().getFirstIndex());
            else
                this.setSelectedIndex(-1);
        }
        return result;
    }

    @Override
    protected void onChartDataChange() {
        super.onChartDataChange();
        setSelectedIndex(-1);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        int oldIndex = this.selectedIndex;
        this.selectedIndex = selectedIndex;
        onIndexChanged(oldIndex, this.selectedIndex);
    }

    public void addSelectedIndexChangeListener(SelectedIndexChangedListener listener)
    {
        if(listener != null)
            this.selectedIndexChangedListeners.add(listener);
    }

    public void removeSelectedIndexChangeListener(SelectedIndexChangedListener listener)
    {
        if(listener != null)
            this.selectedIndexChangedListeners.remove(listener);
    }

    public void clearSelectedIndexChangeListeners()
    {
        this.selectedIndexChangedListeners.clear();
    }

    private void onIndexChanged(int oldIndex, int newIndex)
    {
        for(SelectedIndexChangedListener listener : selectedIndexChangedListeners)
            listener.indexChanged(oldIndex, newIndex);
    }

    public interface SelectedIndexChangedListener
    {
        void indexChanged(int oldIndex, int newIndex);
    }
}
