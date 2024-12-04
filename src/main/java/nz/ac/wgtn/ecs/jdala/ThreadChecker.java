package nz.ac.wgtn.ecs.jdala;

import java.util.concurrent.ConcurrentHashMap;

public class ThreadChecker {
    private static final ConcurrentHashMap<Object, Thread> threadMap = new ConcurrentHashMap<>();

    public static void register(Object localVariable) {
        threadMap.put(localVariable, Thread.currentThread());
    }

    public static void validate(Object localVariable) {
        Thread owner = threadMap.get(localVariable);
        if (owner != Thread.currentThread()) {
            throw new IllegalStateException("Access violation: variable used in a different thread!");
        }
    }

    public static void unregister(Object localVariable) {
        threadMap.remove(localVariable);
    }

    public static void reset(){
        threadMap.clear();
    }
}
