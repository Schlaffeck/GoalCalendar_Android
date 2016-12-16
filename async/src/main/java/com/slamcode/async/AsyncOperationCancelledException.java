package com.slamcode.async;

/**
 * Created by Jan on 2015-06-20.
 */
public class AsyncOperationCancelledException extends Exception
{
    public AsyncOperationCancelledException() {
    }

    public AsyncOperationCancelledException(String detailMessage) {
        super(detailMessage);
    }

    public AsyncOperationCancelledException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AsyncOperationCancelledException(Throwable throwable) {
        super(throwable);
    }
}
