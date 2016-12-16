package com.slamcode.async;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class encapsulating Future task asynchronous operation
 * with callback and internal cancellation support.
 */
public abstract class AsyncOperation<Result>
{
    private Future<Result> futureTask;

    private AsyncOperationStatus status = AsyncOperationStatus.NotStarted;

    private Result result;

    public AsyncOperationStatus getStatus()
    {
        return status;
    }

    public Result getResult()
    {
        return result;
    }

    public void start(final AsyncCancellation cancellation,
                      final AsyncCallback<Result> callback)
    {
        if(this.getStatus() != AsyncOperationStatus.NotStarted)
        {
            throw new IllegalStateException("Can not start operation. Operation is already running or has finished.");
        }

        ExecutorService service = Executors.newFixedThreadPool(1);
        this.futureTask = service.submit(new Callable<Result>()
        {
            @Override
            public Result call() throws Exception
            {
                Result innerResult = null;
                try
                {
                    status = AsyncOperationStatus.Running;
                    AsyncUtils.throwIfCancellationRequested(cancellation);
                    innerResult = runOperation(cancellation, callback);

                    result = innerResult;
                    if(callback != null)
                    {
                        callback.resultCallback(result);
                    }
                    status = AsyncOperationStatus.Finished;
                    return innerResult;
                }
                catch(AsyncOperationCancelledException aoce)
                {
                    if(callback != null)
                    {
                        callback.cancellationCallback();
                    }
                    status = AsyncOperationStatus.Cancelled;
                    throw aoce;
                }
                catch(Exception exc)
                {
                    if(callback != null)
                    {
                        callback.exceptionCallback(exc);
                    }
                    status = AsyncOperationStatus.ExceptionThrown;
                    throw exc;
                }
            }
        });
    }

    protected abstract Result runOperation(AsyncCancellation cancellation,
                                           AsyncCallback<Result> callback)
            throws Exception;

    public boolean hasFinished()
    {
        return this.status.compareTo(AsyncOperationStatus.Running) > 0;
    }

    public void waitForCompletion() throws InterruptedException, ExecutionException {

        if(this.futureTask != null)
        {
           this.result = this.futureTask.get();
        }
        else
        {
            throw new IllegalStateException("Operation has not started yet.");
        }

    }
}
