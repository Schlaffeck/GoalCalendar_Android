package com.slamcode.goalcalendar.view.lists.base;

import java.util.Comparator;

/**
 * Created by moriasla on 08.02.2017.
 */

public class DefaultComparator<Item extends Comparable<Item>> implements Comparator<Item> {
    @Override
    public int compare(Item o1, Item o2) {

        if(o1 == null && o2 == null)
            return 0;

        if(o1 == null)
            return -1;

        if(o2 == null)
            return 1;

        if(o1 == o2)
            return 0;

        return o1.compareTo(o2);
    }
}
