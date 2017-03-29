package com.slamcode.goalcalendar.view.lists.base.bindable;

import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v7.util.SortedList;

import com.slamcode.goalcalendar.view.lists.base.SortedListCallbackSet;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by moriasla on 09.03.2017.
 */

public class ObservableSortedList<Item extends Observable> extends SortedList<Item> {

    private final SortedListCallbackSet<Item> callbackSet;
    private ObservableList<Item> baseList;
    private final ObservableListChangedListener listChangedListener;
    private final ItemPropertyChangedListener propertyChangedListener;
    private boolean preventObservableListChangeNotification = false;
    private boolean observableSourceListChanging = false;

    public ObservableSortedList(ObservableList<Item> baseList, Class<Item> klass, SortedListCallbackSet<Item> callback) {
        super(klass, callback);
        this.baseList = baseList;
        this.listChangedListener = new ObservableListChangedListener();
        this.propertyChangedListener = new ItemPropertyChangedListener();
        for(Item item : baseList)
            this.setupItemPropertyChangeListener(item);
        this.addAll(baseList);
        this.baseList.addOnListChangedCallback(this.listChangedListener);
        this.callbackSet = callback;
    }

    public void updateSourceList(Collection<Item> newSourceList)
    {
        if(newSourceList == this.baseList)
            return;

        if(newSourceList instanceof ObservableList) {
            this.baseList = (ObservableList<Item>) newSourceList;
        }
        else {
            this.baseList = new ObservableArrayList<Item>();
            this.baseList.addAll(newSourceList);
        }
    }

    @Override
    public int add(Item item) {
        this.preventObservableListChangeNotification = true;
        int result = super.add(item);
//        if(!this.observableSourceListChanging)
//            this.baseList.add(item);
        this.preventObservableListChangeNotification = false;
        return result;
    }

    @Override
    public boolean remove(Item item) {
        this.preventObservableListChangeNotification = true;
        boolean result = super.remove(item);
//        if(!this.observableSourceListChanging)
//        this.baseList.remove(item);
        this.preventObservableListChangeNotification = false;
        return result;
    }

    @Override
    public void addAll(Item... items) {
        this.preventObservableListChangeNotification = true;
        super.addAll(items);
//        if(!this.observableSourceListChanging)
//        this.baseList.addAll(Arrays.asList(items));
        this.preventObservableListChangeNotification = false;
    }

    @Override
    public void addAll(Collection<Item> items) {
        this.preventObservableListChangeNotification = true;
        super.addAll(items);
//        if(!this.observableSourceListChanging)
//        this.baseList.addAll(items);
        this.preventObservableListChangeNotification = false;
    }

    @Override
    public void addAll(Item[] items, boolean mayModifyInput) {
        this.preventObservableListChangeNotification = true;
        super.addAll(items, mayModifyInput);
//        if(!this.observableSourceListChanging)
//        this.baseList.addAll(Arrays.asList(items));
        this.preventObservableListChangeNotification = false;
    }

    @Override
    public void clear() {
        this.preventObservableListChangeNotification = true;
        super.clear();
//        if(!this.observableSourceListChanging)
//        this.baseList.clear();
        this.preventObservableListChangeNotification = false;
    }

    @Override
    public Item removeItemAt(int index) {
        this.preventObservableListChangeNotification = true;
        Item result = super.removeItemAt(index);
        if(!this.observableSourceListChanging)
        this.baseList.remove(result);
        this.preventObservableListChangeNotification = false;
        return result;
    }

    @Override
    public void updateItemAt(int index, Item item) {
        this.preventObservableListChangeNotification = true;
        super.updateItemAt(index, item);
        this.preventObservableListChangeNotification = false;
    }

    private void setupItemPropertyChangeListener(Item bindableItem)
    {
        bindableItem.removeOnPropertyChangedCallback(this.propertyChangedListener);
        bindableItem.addOnPropertyChangedCallback(this.propertyChangedListener);
    }

    private void notifyItemMoved(Item item)
    {
        int oldPosition = indexOf(item);
        if(oldPosition != -1)
        {
            recalculatePositionOfItemAt(oldPosition);
        }
        else add(item);
    }

    public SortedListCallbackSet<Item> getCallbackSet() {
        return callbackSet;
    }

    private class ItemPropertyChangedListener extends Observable.OnPropertyChangedCallback {

        @Override
        public void onPropertyChanged(Observable observable, int propertyId) {
            notifyItemMoved((Item)observable);
        }
    }

    private class ObservableListChangedListener extends ObservableList.OnListChangedCallback<ObservableList<Item>>
    {
        @Override
        public void onChanged(ObservableList<Item> items) {
            if(preventObservableListChangeNotification)
                return;
            observableSourceListChanging = true;
            clear();
            addAll(items);
            observableSourceListChanging = false;
        }

        @Override
        public void onItemRangeChanged(ObservableList<Item> items, int startPosition, int count) {
            if(preventObservableListChangeNotification)
                return;
        }

        @Override
        public void onItemRangeInserted(ObservableList<Item> items, int startPosition, int count) {
            if(preventObservableListChangeNotification)
                return;
            observableSourceListChanging = true;
            for (Item item: items.subList(startPosition, startPosition + count)) {
                setupItemPropertyChangeListener(item);
                add(item);
            }
            observableSourceListChanging = false;
        }

        @Override
        public void onItemRangeMoved(ObservableList<Item> items, int startPosition, int movedToPosition, int count) {
            if(preventObservableListChangeNotification)
                return;
            observableSourceListChanging = true;
            for(int i = movedToPosition; i < movedToPosition + count; i++)
            {
                Item item = items.get(i);
                remove(item);
                add(item);
            }
            observableSourceListChanging = false;
        }

        @Override
        public void onItemRangeRemoved(ObservableList<Item> items, int startPosition, int count) {
            if(preventObservableListChangeNotification)
                return;
            observableSourceListChanging = true;
            for(int i = startPosition; i < startPosition + count && i < items.size(); i++)
            {
                remove(items.get(i));
            }
            observableSourceListChanging = false;
        }
    }
}
