package com.slamcode.goalcalendar.android.tasks;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.slamcode.goalcalendar.android.StartForResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public abstract class TaskAbstract<Result> extends Task<Result> {
    protected enum CompletionStatus
    {
        NotStarted(false),
        Working(false),
        Success(true),
        Canceled(true),
        Failed(true);

        private final boolean complete;

        CompletionStatus(boolean complete)
        {
            this.complete = complete;
        }

        public boolean isComplete() {
            return complete;
        }
    }

    private TaskAbstract.CompletionStatus completionStatus = TaskAbstract.CompletionStatus.NotStarted;

    private Exception exception;

    private Result result;

    private List<OnSuccessListener<Result>> onSuccessListeners = new ArrayList<>();
    private List<OnFailureListener> onFailureListeners = new ArrayList<>();
    private List<OnCanceledListener> onCanceledListeners = new ArrayList<>();
    private List<OnCompleteListener> onCompleteListeners = new ArrayList<>();

    private List<Continuation<Result, ?>> continuations = new ArrayList<>();

    protected void setSuccessStatus(Result result)
    {
        if(this.isComplete())
            throw new UnsupportedOperationException("Can not set status for completed task");
        this.completionStatus = TaskAbstract.CompletionStatus.Success;
        this.result = result;
        this.onComplete();
        this.onSuccess();
    }

    @NonNull
    protected void setFailureStatus(Exception exception)
    {
        if(this.isComplete())
            throw new UnsupportedOperationException("Can not set status for completed task");
        this.completionStatus = TaskAbstract.CompletionStatus.Failed;
        this.exception = exception;
        this.onComplete();
        this.onFailure();
    }

    protected void setCanceled()
    {
        if(this.isComplete())
            throw new UnsupportedOperationException("Can not set status for completed task");
        this.completionStatus = TaskAbstract.CompletionStatus.Canceled;
        this.onComplete();
        this.onCancel();
    }

    protected void onFailure() {
        for (OnFailureListener listener :
                this.onFailureListeners) {
            listener.onFailure(this.exception);
        }
    }

    protected void onCancel() {
        for (OnCanceledListener listener :
                this.onCanceledListeners) {
            listener.onCanceled();
        }
    }

    protected void onSuccess() {

        for (OnSuccessListener<Result> listener :
                this.onSuccessListeners) {
            listener.onSuccess(this.result);
        }
    }

    protected void onComplete() {

        for (OnCompleteListener<Result> listener :
                this.onCompleteListeners) {
            listener.onComplete(this);
        }
    }

    protected abstract void start();

    @Override
    public boolean isComplete() {
        return this.completionStatus.isComplete();
    }

    @Override
    public boolean isSuccessful() {
        return this.completionStatus.equals(TaskAbstract.CompletionStatus.Success);
    }

    @Override
    public boolean isCanceled() {
        return this.completionStatus.equals(TaskAbstract.CompletionStatus.Canceled);
    }

    @Nullable
    @Override
    public Result getResult() {
        return this.result;
    }

    @Nullable
    @Override
    public <X extends Throwable> Result getResult(@NonNull Class<X> aClass) throws X {
        return this.result;
    }

    @Nullable
    @Override
    public Exception getException() {
        return this.exception;
    }

    @NonNull
    @Override
    public Task<Result> addOnSuccessListener(@NonNull OnSuccessListener<? super Result> onSuccessListener) {
        this.onSuccessListeners.add((OnSuccessListener<Result>) onSuccessListener);
        return this;
    }

    @NonNull
    @Override
    public Task<Result> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super Result> onSuccessListener) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Task<Result> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super Result> onSuccessListener) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Task<Result> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        this.onFailureListeners.add(onFailureListener);
        return this;
    }

    @NonNull
    @Override
    public Task<Result> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Task<Result> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Task<Result> addOnCompleteListener(@NonNull OnCompleteListener<Result> onCompleteListener) {
        this.onCompleteListeners.add(onCompleteListener);
        return this;
    }
}
