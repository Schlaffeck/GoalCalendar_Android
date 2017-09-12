package com.slamcode.goalcalendar.view.controls;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Extension for standard horizontal scroll view control with scroll changed events
 */

public class ListenableHorizontalScrollView extends HorizontalScrollView {

    private List<OnScrollListener> onScrollListeners = new ArrayList<>();

    public ListenableHorizontalScrollView(Context context) {
        super(context);
    }

    public ListenableHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListenableHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        super.onScrollChanged(l, t, oldl, oldt);

        int dx = l - oldl;
        int dy = t - oldt;

        for(OnScrollListener listener : this.onScrollListeners)
            listener.onScrolled(this, dx, dy);
    }

    public void addOnScrollListener(OnScrollListener listener)
    {
        this.onScrollListeners.add(listener);
    }

    public void removeOnScrollListener(OnScrollListener listener)
    {
        this.onScrollListeners.remove(listener);
    }

    public void clearOnScrollListeners()
    {
        this.onScrollListeners.clear();
    }

    public interface OnScrollListener{

        void onScrolled(ListenableHorizontalScrollView scrollView, int dx, int dy);
    }
}
