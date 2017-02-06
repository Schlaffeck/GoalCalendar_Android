package com.slamcode.goalcalendar.view.lists;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.slamcode.goalcalendar.view.lists.ListViewScrollTracker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by moriasla on 09.01.2017.
 */

public final class ListViewHelper {

    public static void setSimultaneousScrolling(final ListView leftList, final ListView rightList)
    {
        AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {

            boolean isScrolling = false;
            Map<AbsListView, ListViewScrollTracker> trackerMap = new HashMap<>();

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if(this.isScrolling)
                        return;

                this.isScrolling = true;
                if(!trackerMap.containsKey(view))
                {
                    trackerMap.put(view, new ListViewScrollTracker(view));
                }

                ListViewScrollTracker tracker = trackerMap.get(view);
                int offset = tracker.calculateIncrementalOffset(firstVisibleItem, visibleItemCount);
                if(view == leftList)
                    rightList.smoothScrollByOffset(-offset);
                else
                    leftList.smoothScrollByOffset(-offset);
                this.isScrolling = false;
            }
        };

        rightList.setOnScrollListener(scrollListener);
        leftList.setOnScrollListener(scrollListener);
    }

    public static void setSimultaneousScrolling(final RecyclerView leftList, final RecyclerView rightList)
    {
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

            boolean isScrolling = false;

            @Override
            public void onScrollStateChanged(RecyclerView absListView, int i) {

            }

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                if(this.isScrolling)
                    return;

                this.isScrolling = true;
                if(view == leftList)
                    rightList.smoothScrollBy(dx, dy);
                else
                    leftList.smoothScrollBy(dx, dy);
                this.isScrolling = false;
            }
        };

        rightList.addOnScrollListener(scrollListener);
        leftList.addOnScrollListener(scrollListener);
    }
}
