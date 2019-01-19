package com.slamcode.goalcalendar.authentication.impl;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationResult;

import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public abstract class AuthenticationTaskAbstract extends Task<AuthenticationResult> {

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

    private CompletionStatus completionStatus = CompletionStatus.NotStarted;

    private Exception exception;

    private AuthenticationResult result;

    private List<OnSuccessListener<AuthenticationResult>> onSuccessListeners = new ArrayList<>();
    private List<OnFailureListener> onFailureListeners = new ArrayList<>();
    private List<OnCanceledListener> onCanceledListeners = new ArrayList<>();
    private List<OnCompleteListener> onCompleteListeners = new ArrayList<>();

    protected void setSuccessStatus(AuthenticationResult result)
    {
        if(this.isComplete())
            throw new UnsupportedOperationException("Can not set status for completed task");
        this.completionStatus = CompletionStatus.Success;
        this.result = result;
        this.onComplete();
        this.onSuccess();
    }

    @NonNull
    protected void setFailureStatus(Exception exception)
    {
        if(this.isComplete())
            throw new UnsupportedOperationException("Can not set status for completed task");
        this.completionStatus = CompletionStatus.Failed;
        this.exception = exception;
        this.onComplete();
        this.onFailure();
    }

    protected void setCanceled()
    {
        if(this.isComplete())
            throw new UnsupportedOperationException("Can not set status for completed task");
        this.completionStatus = CompletionStatus.Canceled;
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

        for (OnSuccessListener<AuthenticationResult> listener :
                this.onSuccessListeners) {
            listener.onSuccess(this.result);
        }
    }

    protected void onComplete() {

        for (OnCompleteListener<AuthenticationResult> listener :
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
        return this.completionStatus.equals(CompletionStatus.Success);
    }

    @Override
    public boolean isCanceled() {
        return this.completionStatus.equals(CompletionStatus.Canceled);
    }

    @Nullable
    @Override
    public AuthenticationResult getResult() {
        return this.result;
    }

    @Nullable
    @Override
    public <X extends Throwable> AuthenticationResult getResult(@NonNull Class<X> aClass) throws X {
        return this.result;
    }

    @Nullable
    @Override
    public Exception getException() {
        return this.exception;
    }

    @NonNull
    @Override
    public Task<AuthenticationResult> addOnSuccessListener(@NonNull OnSuccessListener<? super AuthenticationResult> onSuccessListener) {
        this.onSuccessListeners.add((OnSuccessListener<AuthenticationResult>) onSuccessListener);
        return this;
    }

    @NonNull
    @Override
    public Task<AuthenticationResult> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super AuthenticationResult> onSuccessListener) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Task<AuthenticationResult> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super AuthenticationResult> onSuccessListener) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Task<AuthenticationResult> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        this.onFailureListeners.add(onFailureListener);
        return this;
    }

    @NonNull
    @Override
    public Task<AuthenticationResult> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public Task<AuthenticationResult> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
        throw new UnsupportedOperationException();
    }
}
