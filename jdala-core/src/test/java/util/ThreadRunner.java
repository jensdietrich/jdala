package util;

public class ThreadRunner {
    public static Throwable runInOtherThread(Runnable runnable) {
        var t1 = new ThreadWithExceptionCapture(runnable);
        t1.start();

        try {
            t1.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return t1.getException();
    }
}
