package nz.ac.wgtn.ecs.jdala.utils;

import java.util.Arrays;

public class PortalClass {
    Class<?> className;
    String[] entryMethods;
    String[] exitMethods;
    Boolean includeSubClasses;

    public PortalClass() {} // Required for JSON deserialization

    public PortalClass(String className, String[] entryMethods, String[] exitMethods, Boolean includeSubClasses) throws ClassNotFoundException {
        this.className = Class.forName(className);
        this.entryMethods = entryMethods;
        this.exitMethods = exitMethods;
        this.includeSubClasses = includeSubClasses;
    }

    public Class<?> getClassName() {
        return className;
    }

    public void setClassName(String className) throws ClassNotFoundException {
        this.className = Class.forName(className);
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

    public Boolean getIncludeSubClasses() {
        return includeSubClasses;
    }

    public void setIncludeSubClasses(Boolean includeSubClasses) {
        this.includeSubClasses = includeSubClasses;
    }

    @Override
    public String toString() {
        return "portalClass{" +
                "className=" + className.getName() +
                ", entryMethods=" + Arrays.toString(entryMethods) +
                ", exitMethods=" + Arrays.toString(exitMethods) +
                ", includeSubClasses=" + includeSubClasses +
                '}';
    }
}
