package com.slamcode.goalcalendar.view.lists.utils;

import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by moriasla on 09.01.2017.
 */

public final class ScrollableViewHelper {

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

    public static void setSimultaneousScrolling(final RecyclerView leftRcv, final RecyclerView rightRcv)
    {
        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView == leftRcv) {
                    rightRcv.removeOnScrollListener(this);
                    rightRcv.scrollBy(0, dy);
                    rightRcv.addOnScrollListener(this);
                } else if (recyclerView == rightRcv) {
                    leftRcv.removeOnScrollListener(this);
                    leftRcv.scrollBy(0, dy);
                    leftRcv.addOnScrollListener(this);
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        };

        leftRcv.addOnScrollListener(scrollListener);
        rightRcv.addOnScrollListener(scrollListener);
    }
}
