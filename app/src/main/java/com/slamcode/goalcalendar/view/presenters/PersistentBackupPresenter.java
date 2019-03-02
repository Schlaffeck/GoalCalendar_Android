package com.slamcode.goalcalendar.view.presenters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.slamcode.collections.CollectionUtils;
import com.slamcode.collections.Predicate;
import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.R;
import com.slamcode.goalcalendar.backup.BackupRestorer;
import com.slamcode.goalcalendar.backup.BackupSourceDataProvider;
import com.slamcode.goalcalendar.backup.BackupSourceDataProvidersRegistry;
import com.slamcode.goalcalendar.backup.BackupWriter;
import com.slamcode.goalcalendar.data.PersistenceContext;
import com.slamcode.goalcalendar.data.model.backup.BackupInfoModel;
import com.slamcode.goalcalendar.data.unitofwork.UnitOfWork;
import com.slamcode.goalcalendar.view.SourceChangeRequestNotifier;
import com.slamcode.goalcalendar.view.activity.BackupActivityContract;
import com.slamcode.goalcalendar.viewmodels.BackupSourceViewModel;
import com.slamcode.goalcalendar.viewmodels.BackupViewModel;

public class PersistentBackupPresenter implements BackupPresenter {

    private static final String LOG_TAG = "GOAL_BackupPres";

    private BackupViewModel data;
    private BackupActivityContract.ActivityView activityView;

    private final ApplicationContext applicationContext;
    private PersistenceContext persistenceContext;
    private final BackupSourceDataProvidersRegistry backupSourceDataProvidersRegistry;

    private final BackupSourceChangeRequestListener backupSourceChangeRequestListener;

    PersistentBackupPresenter(ApplicationContext applicationContext, PersistenceContext persistenceContext, BackupSourceDataProvidersRegistry backupSourceDataProvidersRegistry) {
        this.applicationContext = applicationContext;
        this.persistenceContext = persistenceContext;
        this.backupSourceDataProvidersRegistry = backupSourceDataProvidersRegistry;
        this.backupSourceChangeRequestListener = new BackupSourceChangeRequestListener();
    }

    @Override
    public void setData(BackupViewModel data) {
        if(this.data != data) {
            this.data = data;
            for (BackupSourceViewModel sourceVM :
                    this.data.getBackupSources()) {
                sourceVM.addSourceChangeRequestListener(this.backupSourceChangeRequestListener);
            }
            this.activityView.onDataSet(data);
        }
    }

    @Override
    public void initializeWithView(BackupActivityContract.ActivityView activityView) {
        this.activityView = activityView;
        if(this.data == null)
            this.setData(new BackupViewModel(this.persistenceContext, this.backupSourceDataProvidersRegistry));
        else this.resetData();
    }

    @Override
    public void restoreBackup(String sourceType)
    {
        final BackupSourceDataProvider provider = this.backupSourceDataProvidersRegistry.getProviderByType(sourceType);
        final BackupSourceViewModel viewModel = this.getViewModel(sourceType);
        if(provider == null || viewModel == null) {
            this.applicationContext.showSnackbar(
                    this.activityView.getMainView(),
                    this.applicationContext.getStringFromResources(R.string.backup_no_source_snackbar_message),
                    Snackbar.LENGTH_LONG,
                    this.applicationContext.getStringFromResources(R.string.notification_snackbar_okAction),
                    null);
            return;
        }

        AlertDialog dialog = this.applicationContext.showConfirmDialog(
                this.activityView.getRelatedActivity(),
                this.applicationContext.getStringFromResources(R.string.backup_restore_confirmDialog_title),
                String.format(this.applicationContext.getStringFromResources(R.string.backup_restore_confirmDialog_message), viewModel.getLastBackupDateTime().toString()),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try
                        {
                            data.setProcessingList(true);
                            BackupRestorer.RestoreResult restoreResult = provider.getBackupRestorer().restoreBackup(null);
                            if(restoreResult.isSuccess())
                            {
                                applicationContext.showSnackbar(
                                        activityView.getMainView(),
                                        applicationContext.getStringFromResources(R.string.backup_restore_success_snackbar_message),
                                        Snackbar.LENGTH_LONG,
                                        applicationContext.getStringFromResources(R.string.notification_snackbar_okAction),
                                        null);
                                Log.d(LOG_TAG, "Backup restored");
                            }
                        }
                        catch(Exception e)
                        {
                            applicationContext.showSnackbar(
                                    activityView.getMainView(),
                                    applicationContext.getStringFromResources(R.string.backup_create_error_snackbar_message),
                                    Snackbar.LENGTH_LONG,
                                    applicationContext.getStringFromResources(R.string.notification_snackbar_okAction),
                                    null);
                            Log.d(LOG_TAG, "Backup not restored: " + e.getMessage());
                        }
                        finally {
                            data.setProcessingList(false);
                        }
                    }
                },
                null);
    }

    @Override
    public void doSignIn() {
        this.applicationContext.startLoginActivity(this.activityView.getRelatedActivity(), BackupActivityContract.SHOW_SIGN_IN_ACTIVITY_REQUEST);
    }

    @Override
    public void createBackup(String sourceType) {
        final BackupSourceDataProvider provider = this.backupSourceDataProvidersRegistry.getProviderByType(sourceType);
        if(provider == null) {
            this.applicationContext.showSnackbar(
                    this.activityView.getMainView(),
                    this.applicationContext.getStringFromResources(R.string.backup_no_source_snackbar_message),
                    Snackbar.LENGTH_LONG,
                    this.applicationContext.getStringFromResources(R.string.notification_snackbar_okAction),
                    null);
            return;
        }

        try {
            this.data.setProcessingList(true);
            BackupWriter.WriteResult writeResult = provider.getBackupWriter().writeBackup();
            if(writeResult.isSuccess())
            {
                boolean save = addBackupInfoModel(writeResult.getInfoModel());
                this.applicationContext.showSnackbar(
                        this.activityView.getMainView(),
                        this.applicationContext.getStringFromResources(save ? R.string.backup_create_success_snackbar_message : R.string.backup_create_info_persistence_error_snackbar_message),
                        Snackbar.LENGTH_LONG,
                        this.applicationContext.getStringFromResources(R.string.notification_snackbar_okAction),
                        null);
            }
        }
        catch(Exception e)
        {
            this.applicationContext.showSnackbar(
                    this.activityView.getMainView(),
                    this.applicationContext.getStringFromResources(R.string.backup_create_error_snackbar_message),
                    Snackbar.LENGTH_LONG,
                    this.applicationContext.getStringFromResources(R.string.notification_snackbar_okAction),
                    null);
            Log.d(LOG_TAG, "Backup not saved: " + e.getMessage());
        }
        finally {
            this.data.setProcessingList(false);
        }
    }

    private void resetData() {
        this.activityView.onDataSet(this.data);
    }

    private boolean addBackupInfoModel(final BackupInfoModel infoModel)
    {
        UnitOfWork uow =this.persistenceContext.createUnitOfWork();
            uow.getBackupInfoRepository().add(infoModel);
        uow.complete();

        BackupSourceViewModel viewModel = this.getViewModel(infoModel.getSourceType());

        if(viewModel == null)
            return false;

        viewModel.updateData(infoModel);
        return true;
    }

    private BackupSourceViewModel getViewModel(final String sourceType)
    {
        return CollectionUtils.first(this.data.getBackupSources(), new Predicate<BackupSourceViewModel>() {
            @Override
            public boolean apply(BackupSourceViewModel item) {
                return item.getSourceType().equals(sourceType);
            }
        });
    }

    public class BackupSourceChangeRequestListener implements SourceChangeRequestNotifier.SourceChangeRequestListener<BackupSourceViewModel>
    {
        @Override
        public void sourceChangeRequested(BackupSourceViewModel sender, SourceChangeRequestNotifier.SourceChangeRequest request) {
            if(request.getId() == BackupSourceViewModel.BACKUP_SOURCE_CREATE_BACKUP_REQUEST)
                createBackup(sender.getSourceType());
            if(request.getId() == BackupSourceViewModel.BACKUP_SOURCE_RESTORE_BACKUP_REQUEST)
                restoreBackup(sender.getSourceType());
        }
    }
}
