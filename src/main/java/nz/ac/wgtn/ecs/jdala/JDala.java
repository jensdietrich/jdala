package nz.ac.wgtn.ecs.jdala;

//import com.google.common.collect.MapMaker;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaCapabilityViolationException;
import nz.ac.wgtn.ecs.jdala.exceptions.DalaRestrictionException;
import nz.ac.wgtn.ecs.jdala.utils.CAPABILITY_TYPE;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class JDala {

//    public static final ConcurrentHashMap<Object, Thread> localThreadMap = new ConcurrentHashMap<>();
//    public static final Map<Object, Thread> localThreadMap = new MapMaker().concurrencyLevel(4).weakKeys().makeMap();
//    public static final Map<Object, Thread> localThreadMap = Collections.synchronizedMap(new IdentityHashMap<>());
    public static final Map<Object, Thread> localThreadMap = new IdentityHashMap<>();


    public static final Set<Object> isolatedCollection = Collections.newSetFromMap(new IdentityHashMap<>());
//    public static final Set<Object> immutableObjectsList = Collections.newSetFromMap(new WeakHashMap<Object, Boolean>());

    public static final Set<Object> immutableObjectsList = Collections.newSetFromMap(new IdentityHashMap<>());

//    public static final Set<Object> immutableObjectsList = Collections.newSetFromMap(new MapMaker().concurrencyLevel(4).weakKeys().makeMap());

    /**
     * Injected to designate an Object (and sub-objects) as Immutable Object(s).
     * @param immutableVariable the object that will be constrained to being Immutable
     */
    public static void registerImmutable(Object immutableVariable) {
        if (immutableVariable == null || immutableObjectsList.contains(immutableVariable)) return;

        Set<Object> subObjects = retrieveAllSubObjects(immutableVariable);
        for (Object subObject : subObjects) {
            // Previously registered capabilities are removed assigned Immutable
            if (immutableObjectsList.contains(immutableVariable)) return;
            if (isolatedCollection.contains(subObject)) isolatedCollection.remove(immutableVariable);
            if (localThreadMap.containsKey(subObject)) localThreadMap.remove(immutableVariable);

            immutableObjectsList.add(subObject);
        }
    }

    /**
     * Injected to designate an Object (and sub-objects) as Isolated Object(s).
     * @param isolatedVariable the object that will be constrained to being Isolated
     */
    public static void registerIsolated(Object isolatedVariable) {
        System.out.println("not yet fully implemented");

        if (isolatedVariable == null || isolatedCollection.contains(isolatedVariable)) return;
        if (immutableObjectsList.contains(isolatedVariable)) throw new DalaRestrictionException("Object already registered as Immutable can't register as Isolated: " + isolatedVariable);

        Set<Object> subObjects = retrieveAllSubObjects(isolatedVariable);
        for (Object subObject : subObjects) {
            // Previously registered capabilities are removed assigned Isolated. Immutable can't be converted to Isolated so throws exception.
            if (immutableObjectsList.contains(isolatedVariable)) throw new DalaRestrictionException("Object already registered as Immutable can't register as Isolated: " + isolatedVariable);
            if (isolatedCollection.contains(isolatedVariable)) return;
            if (localThreadMap.containsKey(subObject)) localThreadMap.remove(isolatedVariable);

            isolatedCollection.add(subObject);
        }
    }

    /**
     * Injected to designate an Object (and sub-objects) as Local Object(s).
     * @param localVariable the object that will be constrained to being Local
     */
    public static void registerLocal(Object localVariable) {
        if (localVariable == null || localThreadMap.containsKey(localVariable)) return;

        if (immutableObjectsList.contains(localVariable)) throw new DalaRestrictionException("Already registered as Immutable: " + localVariable);
        if (isolatedCollection.contains(localVariable)) throw new DalaRestrictionException("Already registered as Isolated: " + localVariable);

        Set<Object> subObjects = retrieveAllSubObjects(localVariable);
        for (Object subObject : subObjects) {
            // Immutable or Isolated can't be converted to Isolated so throws exception.
            if (immutableObjectsList.contains(localVariable)) throw new DalaRestrictionException("Already registered as Immutable: " + localVariable);
            if (isolatedCollection.contains(localVariable)) throw new DalaRestrictionException("Already registered as Isolated: " + localVariable);
            if (localThreadMap.containsKey(subObject)) return;

            localThreadMap.put(subObject, Thread.currentThread());
        }
    }

    /**
     *  Will check the highest capability that an object has.
     * @param obj The object to check
     * @return Capability Type of the object
     */
    public static CAPABILITY_TYPE getObjectCapabilityType(Object obj) {
        if (immutableObjectsList.contains(obj)) {return CAPABILITY_TYPE.IMMUTABLE;}
        else if (isolatedCollection.contains(obj)) {return CAPABILITY_TYPE.ISOLATED;}
        else if (localThreadMap.containsKey(obj)) {return CAPABILITY_TYPE.LOCAL;}
        return CAPABILITY_TYPE.UNSAFE;
    }

    /**
     * Checks that a write that is performed is valid
     * This includes checking that the object doesn't violate the constraints of it capability
     * e.g. Local isn't outside of its thread.
     * Any violation of this will throw an {@link DalaCapabilityViolationException} <br>
     *
     * It also checks that the object hierarchy is maintained
     * (imm < iso < local < unsafe)
     * Any violation of this will throw an {@link DalaRestrictionException}
     * @throws DalaCapabilityViolationException When an object violates its capability
     * @throws DalaRestrictionException When a write leads to the breaking of object hierarchy
     * @param objectref the object being written too
     * @param value the value that is being written
     */
    public static void validateWrite(Object objectref, Object value) {
        if (objectref == null) {
            System.out.println("object is null");
            return;
         }
        checkImmutableVariable(objectref);
//        checkImmutableVariable(value);

        checkLocalVariable(objectref);
        checkLocalVariable(value);

        validateObjectPlacement(objectref, value);
    }

    /**
     * Validate read is to assure that Isolated or Local don't violate the constraints of their capacity before a read.
     * @throws DalaCapabilityViolationException When an object violates its capability
     * @param objectref The object that is being read from
     */
    public static void validateRead(Object objectref) {
        if (objectref == null) {
            return;
        }

        checkLocalVariable(objectref);
    }

    /**
     * Check if an object is registered in the {@link #localThreadMap} and if it is then check that it doesn't violate the
     * Local constraint by being in another thread.
     * @throws DalaCapabilityViolationException If the given object violates the Local constraint.
     * @param localVariable The object to validate
     */
    private static void checkLocalVariable(Object localVariable) {
        if (localThreadMap.containsKey(localVariable)) {
            Thread owner = localThreadMap.get(localVariable);
            System.out.println("object is being validated on thread " + Thread.currentThread());
            if (owner != Thread.currentThread()) {
                throw new DalaCapabilityViolationException("Access violation: variable used in a different thread!");
            }
        }
    }

    /**
     * Check if an object is registered in the {@link #immutableObjectsList} and if it is then check that it doesn't violate the
     * Immutable constraint by being edited. Note that this method is called each time a variable is being written so it if it called
     * and the obj is in the list it is a confirmed to be a violation.
     * @throws DalaCapabilityViolationException If the given object violates the Immutable constraint.
     * @param immutableVariable The object to validate
     */
    private static void checkImmutableVariable(Object immutableVariable) {
//        if (immutableVariable == immutableObjectsList) return;
        if (immutableObjectsList.contains(immutableVariable)) {
            throw new DalaCapabilityViolationException("Access violation: Immutable variable can't be edited!");
        }
    }

    /*** Checks that the object hierarchy is maintained (immutable < isolated < local < unsafe)
     * eg an Isolated can only contain references to immutable and other isolated objects, not local or unsafe.
     * Any violation of this will throw an {@link DalaRestrictionException}
     *
     * @throws DalaRestrictionException When a write leads to the breaking of object hierarchy
     * @param objectref the object being written too
     * @param value the value that is being written
     */
    private static void validateObjectPlacement(Object objectref, Object value){
        CAPABILITY_TYPE objectCapabilityType = getObjectCapabilityType(objectref);
        CAPABILITY_TYPE valueType = getObjectCapabilityType(value);
        if ((objectCapabilityType == CAPABILITY_TYPE.ISOLATED && (valueType == CAPABILITY_TYPE.UNSAFE || valueType == CAPABILITY_TYPE.LOCAL)) ||
            (objectCapabilityType == CAPABILITY_TYPE.LOCAL && valueType == CAPABILITY_TYPE.UNSAFE && !(value instanceof String))) {
            throw new DalaRestrictionException("Access violation: object of type: " + valueType + " can't be added to object of type: " + objectCapabilityType);
        }
    }

    /**
     * Reset all the list containing registered objects
     * (used for tests)
     */
    public static void reset(){
        localThreadMap.clear();
        immutableObjectsList.clear();
        isolatedCollection.clear();
    }

    /**
     * Use reflection to get all of the objects that are contained within an Object
     * used for deep immutability, deep isolated, and deep local
     * @param obj the object to get the sub-objects from
     * @return Set of Sub-Objects including itself
     */
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

    /**
     * Helper method for {@link #retrieveAllSubObjects(Object)} to iterate over all array elements
     * @param visited Elements that have been visited
     * @param queue Next elements to be visited
     * @param current Current element
     */
    private static void iterateArray(Set<Object> visited, Queue<Object> queue, Object current) {
        for (int i = 0; i < Array.getLength(current); i++) {
            Object arrayElement = Array.get(current, i);
            if (arrayElement != null && !visited.contains(arrayElement)) {
                visited.add(arrayElement);
                queue.add(arrayElement);
            }
        }
    }

    /**
     * Check if a {@link java.lang.Class} is a primitive or wrapper of a primitive
     * @param clazz The class to check
     * @return If it is a wrapper or primitive
     */
    private static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == String.class ||
                clazz == Boolean.class || Number.class.isAssignableFrom(clazz) ||
                Character.class == clazz;
    }
}
