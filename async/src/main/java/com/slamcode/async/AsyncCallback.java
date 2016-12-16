package com.slamcode.async;

/**
 * Asynchronous future operation resultCallback interface encapsulating method of handling result of
 * operation.
 */
public interface AsyncCallback<T>
{
    void resultCallback(T result);

    void exceptionCallback(Throwable exceptionThrown);

    void cancellationCallback();
}
