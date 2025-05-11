package util;

import java.util.concurrent.atomic.AtomicReference;

public class ThreadWithExceptionCapture extends Thread {
    private final Runnable target;
    private final AtomicReference<Throwable> exception = new AtomicReference<>();

    public ThreadWithExceptionCapture(Runnable target) {
        this.target = target;
    }

    public Throwable getException() {
        return exception.get();
    }

    @Override
    public void run() {
        try {
            target.run();
        } catch (Throwable t) {
            exception.set(t);
            throw t;
        }
    }
}