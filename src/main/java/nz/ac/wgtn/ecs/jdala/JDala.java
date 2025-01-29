package nz.ac.wgtn.ecs.jdala;

import nz.ac.wgtn.ecs.jdala.utils.CAPABILITY_TYPE;

import javax.sound.midi.Soundbank;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JDala {
    // TODO: This may stop the garbage collector from collecting forgotten objects and so will need to be fixed
    public static final ConcurrentHashMap<Object, Thread> localThreadMap = new ConcurrentHashMap<>();
    public static final Collection<Object> isolatedCollection = new HashSet<>();
    public static final Set<Object> immutableObjectsList = Collections.newSetFromMap(new WeakHashMap<Object, Boolean>());
//    public static final

    public static void printTest(){
        System.out.println("\t* JDala injection works");
    }

    public static void registerLocal(Object localVariable) {


        if (localThreadMap.containsKey(localVariable)) {
            System.out.println("Already registered as Local: " + localVariable);
            return;
        } else if (immutableObjectsList.contains(localVariable)) {
            throw new RuntimeException("Already registered as Immutable: " + localVariable);
        } else if (isolatedCollection.contains(localVariable)) {
            throw new RuntimeException("Already registered as Isolated: " + localVariable);
        }


        Set<Object> subObjects = retrieveAllSubObjects(localVariable);
        for (Object subObject : subObjects) {
            localThreadMap.put(subObject, Thread.currentThread());
            System.out.println("\t" + subObject + " is registered as Local on thread " + Thread.currentThread());
        }
    }

    public static void registerImmutable(Object var) {
        if (immutableObjectsList.contains(var)) {
//            System.out.println("Already Immutable registered: " + var);
            return;
        }

        Set<Object> subObjects = retrieveAllSubObjects(var);
        for (Object subObject : subObjects) {
            if (localThreadMap.containsKey(subObject)) {
//                    System.out.println("Already registered as Local, removing from local and assigning Immutable: " + var);
                localThreadMap.remove(var);
            }
            immutableObjectsList.add(subObject);
//                System.out.println("\t" + subObject + " is registered as Immutable");
        }
    }

    public static void registerIsolated(Object localVariable) {
//        if (immutableObjectsList.contains(localVariable)) {
//            System.out.println("Already registered: " + localVariable);
//            return;
//        }
//        immutableObjectsList.add(localVariable);
//        System.out.println(localVariable + " is registered on thread " + Thread.currentThread());
        System.out.println("not yet implemented");
    }

    public static String validateExistingCapabilities(Object obj, CAPABILITY_TYPE newType) {
        CAPABILITY_TYPE existingType = getObjectCapabilityType(obj);
        if (existingType == CAPABILITY_TYPE.UNSAFE) {return null;}
        else if (existingType == newType) {return "Object already registered";}
        else if (existingType.ordinal() > newType.ordinal()) {return "object already has capability of " + existingType + " can't decrease capabilities to " + newType;}
        else if (existingType.ordinal() < newType.ordinal()) {return "object already has capability of " + existingType + " but capabilities changed to " + newType;}
        return "";
    }

    public static CAPABILITY_TYPE getObjectCapabilityType(Object obj) {
        if (immutableObjectsList.contains(obj)) {return CAPABILITY_TYPE.IMMUTABLE;}
        else if (isolatedCollection.contains(obj)) {return CAPABILITY_TYPE.ISOLATED;}
        else if (localThreadMap.containsKey(obj)) {return CAPABILITY_TYPE.LOCAL;}
        return CAPABILITY_TYPE.UNSAFE;
    }

    public static void validateWrite(Object objectref, Object value) {
        if (objectref == null) {
            System.out.println("object is null");
            return;
        }
        checkImmutableVariable(objectref);
        checkLocalVariable(objectref);
    }

    public static void validateRead(Object objectref) {
//        checkLocalVariable(objectref);
    }

    public static void validateConstructor(Object objectref) {
//        System.out.println("\t" + obj);

        if (objectref == null) {
            System.out.println("object is null");
            return;
        }
    }

    public static boolean checkLocalVariable(Object localVariable) {
        try {
            if (localThreadMap.containsKey(localVariable)) {
                Thread owner = localThreadMap.get(localVariable);
                System.out.println("object is being validated on thread " + Thread.currentThread());
                if (owner != Thread.currentThread()) {
                    throw new IllegalStateException("Access violation: variable used in a different thread!");
                }
                return true;
            }
        } catch (NullPointerException e) {
            // TODO: find more permanent solution https://github.com/jensdietrich/jdala/issues/9
//            System.out.println("Likely Hashcode fail \"" + e.getMessage() + "\"");
            return false;
        }
        return false;
    }

    public static boolean checkImmutableVariable(Object immutableVariable) {
            if (immutableObjectsList.contains(immutableVariable)) {
                throw new IllegalStateException("Access violation: Immutable variable can't be edited!");
            }
        return false;
    }

    public static void reset(){
        localThreadMap.clear();
        immutableObjectsList.clear();
        isolatedCollection.clear();
    }

    public static Set<Object> retrieveAllSubObjects(Object obj) {

        Set<Object> visited = new HashSet<>();
        Queue<Object> queue = new LinkedList<>();

        queue.add(obj);
        visited.add(obj);

        if (isPrimitiveOrWrapper(obj.getClass())) {
            return visited;
        }

        try {
            while (!queue.isEmpty()) {
                Object current = queue.poll();
                Class<?> clazz = current.getClass();

                while (clazz != null) {
                    for (Field field : clazz.getDeclaredFields()) {
                        field.setAccessible(true);
                        Object fieldValue = field.get(current);

                        if (fieldValue != null && !visited.contains(fieldValue)) {
                            Class<?> fieldClazz = fieldValue.getClass();
                            visited.add(fieldValue);
                            if (!isPrimitiveOrWrapper(fieldClazz)) {
                                queue.add(fieldValue);
                            }
                        }
                    }

                    if (clazz.isArray()) {
                        iterateArray(visited, queue, current);
                    }

                    clazz = clazz.getSuperclass();
                }
            }

            return visited;
        } catch (IllegalAccessException e) {
//            System.err.println("Error while retrieving variable sub-objects: " + localVariable);
//            localThreadMap.put(localVariable, Thread.currentThread());
//            System.out.println(localVariable + " is registered as Local on thread " + Thread.currentThread());
        throw new RuntimeException("Error while retrieving variable sub-objects: " + obj +
                "\nPlease make sure that pom (or command line args) allows reflection to access sub-objects", e);
        }
    }

    private static void iterateArray(Set<Object> visited, Queue<Object> queue, Object current) {
        for (int i = 0; i < Array.getLength(current); i++) {
            Object arrayElement = Array.get(current, i);
            if (arrayElement != null && !visited.contains(arrayElement)) {
                visited.add(arrayElement);
                queue.add(arrayElement);
            }
        }
    }

    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == String.class ||
                clazz == Boolean.class || Number.class.isAssignableFrom(clazz) ||
                Character.class == clazz;
    }


}
