package kin.utils;


import androidx.annotation.VisibleForTesting;

import org.kin.sdk.base.tools.Promise;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.jvm.functions.Function1;


/**
 * Represents method invocation, each request will run sequentially on background thread,
 * and will notify {@link ResultCallback} witch success or error on main thread.
 *
 * @param <T> request result type
 */
public class Request<T> {
    private static final ExecutorService workInBackgroundExecutor = Executors.newSingleThreadExecutor();
    private static ExecutorService callbackExecutor = new MainExecutorService();
    private Promise<T> request;
    private Function1<Exception, Exception> mapError;

    @VisibleForTesting
    public static void setCallbackExecutor(ExecutorService es) {
        callbackExecutor = es;
    }

    /**
     * Should not be constructing these outside the base-compat sdk implementation.
     * Here for to support the interface in posterity only.
     *
     * @deprecated don't use this, it's not functional
     */
    @Deprecated
    public Request(Callable<T> callable) {
        /* DO NOTHING */
    }

    public Request(Promise<T> request, Function1<Exception, Exception> mapError) {
        this.request = request;
        this.mapError = mapError;
    }

    /**
     * Run request asynchronously, notify {@code callback} with successful result or error
     */
    public void run(ResultCallback<T> callback) {
        if (request != null) {
            request.then(result -> {
                try {
                    callbackExecutor.submit(() -> callback.onResult(result));
                } catch (final Exception e) {
                    Exception error;
                    if (mapError != null) {
                        error = mapError.invoke(e);
                    } else {
                        error = e;
                    }
                    callbackExecutor.submit(() -> callback.onError(error));
                }
                return null;
            }, throwable -> {
                Exception exception;
                if (throwable instanceof Exception) {
                    exception = (Exception) throwable;
                } else {
                    exception = new RuntimeException(throwable);
                }
                callbackExecutor.submit(() -> callback.onError(mapError.invoke(exception)));
                return null;
            });
        }
    }

    /**
     * Here for to support the interface in posterity only.
     *
     * @deprecated don't use this, it's not functional
     */
    @Deprecated
    public void cancel(boolean mayInterruptIfRunning) {
    }
}
