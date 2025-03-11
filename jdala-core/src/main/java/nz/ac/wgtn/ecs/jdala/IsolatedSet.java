package nz.ac.wgtn.ecs.jdala;


import shaded.org.plumelib.util.WeakIdentityHashMap;

import java.util.*;

/**
 * @author Quinten Smit
 */
public class IsolatedSet extends AbstractSet<Object>{
    private Set<Object> objectSet = Collections.newSetFromMap(shaded.java.util.Collections.synchronizedMap(new WeakIdentityHashMap<Object, Boolean>()));
    private Thread currentThread = Thread.currentThread();
    private boolean transferState = false;

    public IsolatedSet(){}

    public IsolatedSet(Collection<Object> objectSet) {
        this.objectSet.addAll(objectSet);
    }

    public Thread getCurrentThread() {
        return currentThread;
    }

    public boolean isInTransferState() {
        return transferState;
    }

    public boolean contains(Object o){
        return objectSet.contains(o);
    }

    public boolean add(Object o){
        return objectSet.add(o);
    }

    public boolean remove(Object o){
        return objectSet.remove(o);
    }

    public boolean isEmpty(){
        return objectSet.isEmpty();
    }

    @Override
    public int size() {
        return objectSet.size();
    }

    @Override
    public Iterator<Object> iterator() {
        return objectSet.iterator();
    }

    public String toString(){
        return "{Thread: " + currentThread + " TransferState:" + transferState + " Contained Objects" + objectSet.toString() + "}";
    }
}
