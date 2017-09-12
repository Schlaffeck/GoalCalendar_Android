package com.slamcode.goalcalendar.view;

import android.databinding.BindingAdapter;
import android.widget.HorizontalScrollView;

import com.slamcode.goalcalendar.view.controls.ListenableHorizontalScrollView;
import com.slamcode.goalcalendar.view.lists.scrolling.HorizontalScrollViewSimultaneousScrollingController;

import java.util.HashMap;
import java.util.Map;

public class ScrollGroupBinding {

    private static Map<String, HorizontalScrollViewSimultaneousScrollingController> HorizontalScrollViewSimultaneousScrollingControllerMap = new HashMap<>();

    @BindingAdapter("bind:scrollGroupName")
    public static void setScrollGroup(ListenableHorizontalScrollView scrollView, String scrollGroupName)
    {
        if(!HorizontalScrollViewSimultaneousScrollingControllerMap.containsKey(scrollGroupName))
        {
            HorizontalScrollViewSimultaneousScrollingControllerMap.put(scrollGroupName, new HorizontalScrollViewSimultaneousScrollingController());
        }

        HorizontalScrollViewSimultaneousScrollingControllerMap.get(scrollGroupName).addForSimultaneousScrolling(scrollView);
    }
}
