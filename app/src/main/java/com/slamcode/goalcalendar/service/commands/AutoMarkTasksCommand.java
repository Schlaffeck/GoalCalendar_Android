package com.slamcode.goalcalendar.service.commands;

import android.view.View;

import com.slamcode.goalcalendar.commands.Command;

/**
 * Created by moriasla on 10.02.2017.
 */

public interface AutoMarkTasksCommand extends Command<View, AutoMarkTasksCommand.AutoMarkResult> {

    class AutoMarkResult{

        private boolean wasRun;

        private int unfinishedTasksMarkedFailedCount;

        public int getUnfinishedTasksMarkedFailedCount() {
            return unfinishedTasksMarkedFailedCount;
        }

        public void setUnfinishedTasksMarkedFailedCount(int unfinishedTasksMarkedFailedCount) {
            this.unfinishedTasksMarkedFailedCount = unfinishedTasksMarkedFailedCount;
        }

        public boolean getWasRun() {
            return wasRun;
        }

        public void setWasRun(boolean wasRun) {
            this.wasRun = wasRun;
        }
    }
}
