package com.slamcode.goalcalendar.view.lists.scrolling;

import android.view.ViewGroup;

public interface SimultaneousScrollingController<ScrollableControl extends ViewGroup> {

    void addForSimultaneousScrolling(ScrollableControl control);

    void removeFromSimultaneousScrolling(ScrollableControl control);

    void clearScrollableControls();
}
