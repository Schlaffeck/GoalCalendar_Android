package com.slamcode.goalcalendar.view;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GlobalViewProcessingState implements ViewProcessingState {

    private List<ChangeListener> changeListeners = new ArrayList<>();
    private Map<String, Integer> processedViewCounterRegister = new ConcurrentHashMap<>();

    @Override
    public synchronized boolean isProcessingView() {
        return processedViewCounterRegister.isEmpty();
    }

    @Override
    public synchronized void startProcessingView(String viewId) {
        if(viewId == null)
            return;

        if(this.processedViewCounterRegister.containsKey(viewId))
            this.processedViewCounterRegister.put(viewId, this.processedViewCounterRegister.get(viewId) + 1);
        else {
            this.processedViewCounterRegister.put(viewId, 1);
        }

        if(this.processedViewCounterRegister.size() == 1)
            this.onStartedProcessingViews();
    }

    @Override
    public synchronized void stopProcessingView(String viewId) {
            if(viewId == null)
                return;

        if(this.processedViewCounterRegister.containsKey(viewId)) {

            Integer currentCounter = this.processedViewCounterRegister.get(viewId);
            if(currentCounter == 1)
                this.processedViewCounterRegister.remove(viewId);
            else
                this.processedViewCounterRegister.put(viewId, currentCounter - 1);
        }

        if(this.processedViewCounterRegister.size() == 0)
            this.onStoppedProcessingAllViews();
    }

    @Override
    public void addChangeListener(ChangeListener listener) {

        this.changeListeners.add(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {

        this.changeListeners.remove(listener);
    }

    @Override
    public void clearChangeListeners() {

        this.changeListeners.clear();
    }

    private void onStoppedProcessingAllViews()
    {
        for(ChangeListener listener : this.changeListeners)
            listener.onStoppedProcessingAllViews();
    }

    private void onStartedProcessingViews()
    {
        for(ChangeListener listener : this.changeListeners)
            listener.onStartedProcessingViews();
    }
}
