package com.slamcode.async;

/**
 * Created by Jan on 2015-06-20.
 */
public class AsyncUtils
{
    public static void throwIfCancellationRequested(AsyncCancellation cancellation) throws AsyncOperationCancelledException
    {
        if(cancellation !=null && cancellation.isCancellationRequested())
        {
            throw new AsyncOperationCancelledException();
        }
    }
}
