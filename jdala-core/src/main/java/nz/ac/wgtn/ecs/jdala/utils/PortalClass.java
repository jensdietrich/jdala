package nz.ac.wgtn.ecs.jdala.utils;

import java.util.Map;

/**
 * @author Quinten Smit
 */
public class PortalClass {
    private final String className;
    // Currently this doesn't allow multiple methods with the same name and different descriptors
    private final Map<String, PortalMethod> entryMethods;
    private final Map<String, PortalMethod> exitMethods;
    private final boolean includeSubClasses;

    public PortalClass(String className,  Map<String, PortalMethod> entryMethods, Map<String, PortalMethod> exitMethods, boolean includeSubClasses) {
        this.className = className;
        this.entryMethods = entryMethods;
        this.exitMethods = exitMethods;
        this.includeSubClasses = includeSubClasses;
    }

    public String getClassName() {
        return className;
    }

    /**
     *
     * @param methodName
     * @param descriptor
     * @return
     */
    public PortalMethod getPortalMethod(String methodName, String descriptor) {
        PortalMethod pm = entryMethods.get(methodName);
        if (pm == null) {
            pm = exitMethods.get(methodName);
        } if (pm != null && !pm.getDescriptor().equals(descriptor)) {
            return null;
        }
        return pm;
    }

    public Boolean includesSubClasses() {
        return includeSubClasses;
    }

    @Override
    public String toString() {
        return "portalClass{" +
                "className=" + className +
                ", entryMethods=" + entryMethods +
                ", exitMethods=" + exitMethods +
                ", includeSubClasses=" + includeSubClasses +
                '}';
    }
}
