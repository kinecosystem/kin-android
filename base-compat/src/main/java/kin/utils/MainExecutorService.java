package kin.utils;

import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class MainExecutorService implements ExecutorService {


    static class MainExecutor implements Executor {
        private Handler mainHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NotNull Runnable command) {
            mainHandler.post(command);
        }
    }

    private MainExecutor executor;

    private MainExecutor getExecutor() {
        if (executor == null) {
            executor = new MainExecutor();
        }
        return executor;
    }

    @Override
    public void shutdown() {

    }

    @NotNull
    @Override
    public List<Runnable> shutdownNow() {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        return false;
    }

    @NotNull
    @Override
    public <T> Future<T> submit(@NotNull Callable<T> task) {
        FutureTask<T> futureTask = new FutureTask<>(task);
        getExecutor().execute(futureTask);
        return futureTask;
    }

    @NotNull
    @Override
    public <T> Future<T> submit(@NotNull Runnable task, T result) {
        FutureTask<T> futureTask = new FutureTask<>(task, result);
        getExecutor().execute(futureTask);
        return futureTask;
    }

    @NotNull
    @Override
    public Future<?> submit(@NotNull Runnable task) {
        return submit(task, null);
    }

    @NotNull
    @Override
    public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new RuntimeException("Not Implemented");
    }

    @NotNull
    @Override
    public <T> List<Future<T>> invokeAll(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws InterruptedException {
        throw new RuntimeException("Not Implemented");
    }

    @NotNull
    @Override
    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks) throws ExecutionException, InterruptedException {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public <T> T invokeAny(@NotNull Collection<? extends Callable<T>> tasks, long timeout, @NotNull TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public void execute(@NotNull Runnable command) {
        getExecutor().execute(command);
    }
}
