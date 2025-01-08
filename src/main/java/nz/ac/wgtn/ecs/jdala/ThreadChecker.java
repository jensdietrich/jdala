package nz.ac.wgtn.ecs.jdala;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadChecker {
    // TODO: This may stop the garbage collector from collecting forgotten objects and so will need to be fixed
    private static final ConcurrentHashMap<Object, Thread> localThreadMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Object, Thread> immutableThreadMap = new ConcurrentHashMap<>();


    public static void registerLocal(Object localVariable) {
        if (localThreadMap.containsKey(localVariable)) {
            System.out.println("Already registered: " + localVariable);
            return;
        }
        try {
            Set<Object> subObjects = retrieveAllSubObjects(localVariable);
            for (Object subObject : subObjects) {
                localThreadMap.put(subObject, Thread.currentThread());
                System.out.println("\t" + subObject + " is registered on thread " + Thread.currentThread());
            }
        } catch (IllegalAccessException e) {
            System.out.println("Error while retrieving variable sub-objects: " + localVariable);
            localThreadMap.put(localVariable, Thread.currentThread());
            System.out.println(localVariable + " is registered on thread " + Thread.currentThread());
            throw new RuntimeException(e);
        }

    }

    public static void registerImmutable(Object localVariable) {
        if (immutableThreadMap.containsKey(localVariable)) {
            System.out.println("Already registered: " + localVariable);
            return;
        }
        immutableThreadMap.put(localVariable, Thread.currentThread());
        System.out.println(localVariable + " is registered on thread " + Thread.currentThread());
    }

    public static void validate(Object localVariable) {
        Thread owner = localThreadMap.get(localVariable);
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
        localThreadMap.remove(localVariable);
    }

    public static void reset(){
        localThreadMap.clear();
    }

    public static Set<Object> retrieveAllSubObjects(Object obj) throws IllegalAccessException {
        Set<Object> visited = new HashSet<>();
        Queue<Object> queue = new LinkedList<>();

        queue.add(obj);
        visited.add(obj);

        while (!queue.isEmpty()) {
            Object current = queue.poll();

            Class<?> clazz = current.getClass();
            while (clazz != null) {
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);

                    Object fieldValue = field.get(current);

                    if (fieldValue != null && !visited.contains(fieldValue)) {
                        if (isPrimitiveOrWrapper(fieldValue.getClass())) {
                            continue;
                        }

                        visited.add(fieldValue);
                        queue.add(fieldValue);

                        // collections, maps, and arrays
                        if (fieldValue instanceof Collection) {
                            queue.addAll((Collection<?>) fieldValue);
                        } else if (fieldValue instanceof Map) {
                            queue.addAll(((Map<?, ?>) fieldValue).keySet());
                            queue.addAll(((Map<?, ?>) fieldValue).values());
                        } else if (fieldValue.getClass().isArray()) {
                            for (int i = 0; i < Array.getLength(fieldValue); i++) {
                                Object arrayElement = Array.get(fieldValue, i);
                                if (arrayElement != null && !visited.contains(arrayElement)) {
                                    visited.add(arrayElement);
                                    queue.add(arrayElement);
                                }
                            }
                        }
                    }
                }

                clazz = clazz.getSuperclass();
            }
        }

        return visited;
    }

    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == String.class ||
                clazz == Boolean.class || Number.class.isAssignableFrom(clazz) ||
                Character.class == clazz;
    }


}
