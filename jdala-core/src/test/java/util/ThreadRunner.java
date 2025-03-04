package util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadRunner {
    public static Throwable runInOtherThread(Runnable runnable) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<?> future = es.submit(runnable);

        try {
            future.get();
        } catch (ExecutionException e) {
            return e.getCause();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
