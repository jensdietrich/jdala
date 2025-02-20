package nz.ac.wgtn.ecs.jdala;


import java.util.*;

public class IsolatedSet extends AbstractSet<Object>{
    Set<Object> objectSet = Collections.newSetFromMap(new IdentityHashMap<Object, Boolean>());
    Thread currentThread = Thread.currentThread();
    boolean transferState = false;

    public IsolatedSet(Collection<Object> objectSet) {
        this.objectSet.addAll(objectSet);
    }

    public boolean contains(Object o){
        return objectSet.contains(o);
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
}
