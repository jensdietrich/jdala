package nz.ac.wgtn.ecs.jdala.utils;

import java.util.Set;

/**
 * @author Quinten Smit
 */
public class PortalClass {
    private final String className;
    // Currently this doesn't allow multiple methods with the same name and different descriptors
    private final Set<PortalMethod> methods;
    private final boolean includeSubClasses;

    public PortalClass(String className, Set<PortalMethod> methods, boolean includeSubClasses) {
        this.className = className;
        this.includeSubClasses = includeSubClasses;
        this.methods = methods;
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
        for (PortalMethod method : methods) {
            if (method.getMethodName().equals(methodName) && (method.getDescriptor().isEmpty() || descriptor.equals(method.getDescriptor()))) {
                return method;
            }
        }
        return null;
    }

    public Boolean includesSubClasses() {
        return includeSubClasses;
    }

    @Override
    public String toString() {
        return "portalClass{" +
                "className=" + className +
                ", methods=" + methods +
                ", includeSubClasses=" + includeSubClasses +
                '}';
    }
}
