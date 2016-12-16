package com.slamcode.async;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Jan on 2015-06-20.
 */
public class AsyncCancellation
{
    private AtomicBoolean cancellationRequested = new AtomicBoolean(false);

    public boolean isCancellationRequested()
    {
        return cancellationRequested.get();
    }

    public void requestCancellation()
    {
       this.cancellationRequested.set(true);
    }
}
