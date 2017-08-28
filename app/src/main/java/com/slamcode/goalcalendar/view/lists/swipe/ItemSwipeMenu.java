package com.slamcode.goalcalendar.view.lists.swipe;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Represents data for swipe menu attached to item
 */

public class ItemSwipeMenu {

    private final ViewGroup menuLayoutView;
    private List<SwipeMenuOption> menuOptions = new ArrayList<>();

    private View itemView;

    public ItemSwipeMenu(View itemView, ViewGroup menuLayoutView, SwipeMenuOption... options) {
        this.itemView = itemView;
        this.menuLayoutView = menuLayoutView;
        for(SwipeMenuOption option : options)
            menuOptions.add(option);
        this.setUpMenuLayout();
    }

    public void addMenuOption(SwipeMenuOption option)
    {
        menuOptions.add(option);
        this.menuLayoutView.addView(option.inflateView(this.itemView.getContext()));
    }

    public void clearMenuOptions()
    {
        this.menuOptions.clear();
        this.setUpMenuLayout();
    }

    public void showMenu()
    {
        this.menuLayoutView.setVisibility(View.VISIBLE);
    }

    public void hideMenu()
    {
        this.menuLayoutView.setVisibility(View.GONE);
    }

    private void setUpMenuLayout()
    {
        for(SwipeMenuOption option : menuOptions)
            this.addMenuOption(option);
    }
}
