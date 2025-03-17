package nz.ac.wgtn.ecs.jdala.utils;

import java.util.Arrays;

/**
 * @author Quinten Smit
 */
public class PortalClass {
    String className;
    String[] entryMethods;
    String[] exitMethods;
    Boolean includeSubClasses;

    public PortalClass() {}

    public PortalClass(String className, String[] entryMethods, String[] exitMethods, Boolean includeSubClasses) throws ClassNotFoundException {
        this.className = className;
        this.entryMethods = entryMethods;
        this.exitMethods = exitMethods;
        this.includeSubClasses = includeSubClasses;
    }

    public String getClazz() {
        return className;
    }

    public String[] getEntryMethods() {
        return entryMethods;
    }

    public void setEntryMethods(String[] entryMethods) {
        this.entryMethods = entryMethods;
    }

    public String[] getExitMethods() {
        return exitMethods;
    }

    public void setExitMethods(String[] exitMethods) {
        this.exitMethods = exitMethods;
    }

    public Boolean includesSubClasses() {
        return includeSubClasses;
    }

//    public void setIncludeSubClasses(Boolean includeSubClasses) {
//        this.includeSubClasses = includeSubClasses;
//    }

    @Override
    public String toString() {
        return "portalClass{" +
                "className=" + className +
                ", entryMethods=" + Arrays.toString(entryMethods) +
                ", exitMethods=" + Arrays.toString(exitMethods) +
                ", includeSubClasses=" + includeSubClasses +
                '}';
    }
}
