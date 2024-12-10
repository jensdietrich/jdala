package nz.ac.wgtn.ecs.jdala;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadChecker {
    // TODO: This may stop the garbage collector from collecting forgotten objects and so will need to be fixed
    private static final ConcurrentHashMap<Object, Thread> threadMap = new ConcurrentHashMap<>();

    public static void register(Object localVariable) {
        if (threadMap.containsKey(localVariable)) {
            System.out.println("Already registered: " + localVariable);
            return;
        }
        threadMap.put(localVariable, Thread.currentThread());
        System.out.println(localVariable + " is registered on thread " + Thread.currentThread());
    }

    public static void validate(Object localVariable) {
        Thread owner = threadMap.get(localVariable);
        if (owner == null) {
            System.out.println("Variable " + localVariable + " is not registered!");
            return;
        }
        System.out.println(localVariable + " is being validated on thread " + Thread.currentThread());
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
