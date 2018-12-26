package com.slamcode.goalcalendar.view.lists;

import android.databinding.ObservableArrayList;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.view.BaseSourceChangeRequest;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableRecyclerViewDataAdapter;
import com.slamcode.goalcalendar.view.lists.base.bindable.BindableViewHolderBase;
import com.slamcode.goalcalendar.viewmodels.BackupSourceViewModel;

public class BackupSourcesRecyclerViewAdapter extends BindableRecyclerViewDataAdapter<BackupSourceViewModel, BackupSourcesRecyclerViewAdapter.BackupSourceViewHolder> {

    protected BackupSourcesRecyclerViewAdapter() {
        super(new ObservableArrayList<BackupSourceViewModel>());
    }

    @NonNull
    @Override
    public BackupSourceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_backup_source_linear_layout, null);
        return new BackupSourceViewHolder(view);
    }

    public class BackupSourceViewHolder extends BindableViewHolderBase<BackupSourceViewModel> {

        public BackupSourceViewHolder(View view) {
            super(view);
        }

        @Override
        public void bindToModel(BackupSourceViewModel modelObject) {
            super.bindToModel(modelObject);
        }

        public void requestBackupCreation()
        {
            this.getModelObject().notifySourceChangeRequested(new BaseSourceChangeRequest(BackupSourceViewModel.BACKUP_SOURCE_CREATE_BACKUP_REQUEST));
        }

        public void requestBackupRestoration()
        {
            this.getModelObject().notifySourceChangeRequested(new BaseSourceChangeRequest(BackupSourceViewModel.BACKUP_SOURCE_RESTORE_BACKUP_REQUEST));
        }
    }
}
