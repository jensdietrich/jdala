package util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadRunner {
    public static Throwable runInOtherThread(Runnable runnable) throws Throwable {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<?> future = es.submit(runnable);

        try {
            future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
        return null;
    }
}
