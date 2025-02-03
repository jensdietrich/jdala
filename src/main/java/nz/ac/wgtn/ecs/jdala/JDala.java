package nz.ac.wgtn.ecs.jdala;

//import com.google.common.collect.MapMaker;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaRestrictionException;
import nz.ac.wgtn.ecs.jdala.utils.CAPABILITY_TYPE;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class JDala {
    // TODO: This may stop the garbage collector from collecting forgotten objects and so will need to be fixed

//    public static final ConcurrentHashMap<Object, Thread> localThreadMap = new ConcurrentHashMap<>();
//    public static final Map<Object, Thread> localThreadMap = new MapMaker().concurrencyLevel(4).weakKeys().makeMap();
//    public static final Map<Object, Thread> localThreadMap = Collections.synchronizedMap(new IdentityHashMap<>());
    public static final Map<Object, Thread> localThreadMap = new IdentityHashMap<>();


    public static final Set<Object> isolatedCollection = new HashSet<>();
//    public static final Set<Object> immutableObjectsList = Collections.newSetFromMap(new WeakHashMap<Object, Boolean>());

    public static final Set<Object> immutableObjectsList = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());

//    public static final Set<Object> immutableObjectsList = Collections.newSetFromMap(new MapMaker().concurrencyLevel(4).weakKeys().makeMap());

    public static void registerLocal(Object localVariable) {
        if (localVariable == null || localThreadMap.containsKey(localVariable)) return;

        if (immutableObjectsList.contains(localVariable)) {
            throw new DalaRestrictionException("Already registered as Immutable: " + localVariable);
        } else if (isolatedCollection.contains(localVariable)) {
            throw new DalaRestrictionException("Already registered as Isolated: " + localVariable);
        }

        Set<Object> subObjects = retrieveAllSubObjects(localVariable);
        for (Object subObject : subObjects) {
            localThreadMap.put(subObject, Thread.currentThread());
            System.out.println("\t" + subObject + " is registered as Local on thread " + Thread.currentThread());
        }
    }

    public static void registerImmutable(Object immutableVariable) {
        if (immutableVariable == null || immutableObjectsList.contains(immutableVariable)) return;

        Set<Object> subObjects = retrieveAllSubObjects(immutableVariable);
        for (Object subObject : subObjects) {
            if (localThreadMap.containsKey(subObject)) {
//                    System.out.println("Already registered as Local, removing from local and assigning Immutable: " + var);
                localThreadMap.remove(immutableVariable);
            }
            if (isolatedCollection.contains(subObject)) {
                isolatedCollection.remove(immutableVariable);
            }
            immutableObjectsList.add(subObject);
//                System.out.println("\t" + subObject + " is registered as Immutable");
        }
    }

    public static void registerIsolated(Object isolatedVariable) {
        System.out.println("not yet implemented");

        if (isolatedVariable == null || isolatedCollection.contains(isolatedVariable)) return;

        Set<Object> subObjects = retrieveAllSubObjects(isolatedVariable);
        for (Object subObject : subObjects) {
            if (immutableObjectsList.contains(isolatedVariable) || isolatedCollection.contains(isolatedVariable)) return;
            if (localThreadMap.containsKey(subObject)) {
                localThreadMap.remove(isolatedVariable);
            }
            isolatedCollection.add(subObject);
        }
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
        checkImmutableVariable(value);

        checkLocalVariable(objectref);
        checkLocalVariable(value);

        validateObjectPlacement(objectref, value);
    }

    public static void validateRead(Object objectref) {
        if (objectref == null) {
            return;
        }

        checkLocalVariable(objectref);
    }

    public static void validateConstructor(Object thisObject) {

//        for (Object subObj : retrieveAllSubObjects(thisObject)){
//            checkLocalVariable(subObj);
//        }
    }

    private static boolean checkLocalVariable(Object localVariable) {
        if (localThreadMap.containsKey(localVariable)) {
            Thread owner = localThreadMap.get(localVariable);
            System.out.println("object is being validated on thread " + Thread.currentThread());
            if (owner != Thread.currentThread()) {
                throw new DalaCapabilityViolationException("Access violation: variable used in a different thread!");
            }
            return true;
        }
        return false;
    }

    private static boolean checkImmutableVariable(Object immutableVariable) {
        if (immutableVariable == immutableObjectsList) return false;
        if (immutableObjectsList.contains(immutableVariable)) {
            throw new DalaCapabilityViolationException("Access violation: Immutable variable can't be edited!");
        }
        return false;
    }

    private static boolean validateObjectPlacement(Object objectref, Object value){
        CAPABILITY_TYPE objectCapabilityType = getObjectCapabilityType(objectref);
        CAPABILITY_TYPE valueType = getObjectCapabilityType(value);
        if ((objectCapabilityType == CAPABILITY_TYPE.ISOLATED && (valueType == CAPABILITY_TYPE.UNSAFE || valueType == CAPABILITY_TYPE.LOCAL)) ||
            (objectCapabilityType == CAPABILITY_TYPE.LOCAL && valueType == CAPABILITY_TYPE.UNSAFE && !(value instanceof String))) {
            throw new DalaRestrictionException("Access violation: object of type: " + valueType + " can't be added to object of type: " + objectCapabilityType);
        }
        return true;
    }

    public static void reset(){
        localThreadMap.clear();
        immutableObjectsList.clear();
        isolatedCollection.clear();
    }

    private static Set<Object> retrieveAllSubObjects(Object obj) {
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
