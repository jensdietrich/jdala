package nz.ac.wgtn.ecs.jdala.utils;

public class PortalMethod {
    private final String methodName;
    private final int parameterIndex;
    private final String descriptor ;
    private final boolean isEntry;

    public PortalMethod(String methodName, int parameterIndex, String descriptor, boolean isEntry) {
        this.methodName = methodName;
        this.parameterIndex = parameterIndex;
        if (descriptor == null) descriptor = "";
        this.descriptor = descriptor;
        this.isEntry = isEntry;
    }

    public PortalMethod(String methodName, String descriptor, boolean isEntry) {
        this(methodName, -1, descriptor, isEntry);
    }

    public String getMethodName() {
        return methodName;
    }

    public int getParameterIndex() {
        return parameterIndex;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public boolean isExitPortal(){
        return parameterIndex == -1;
    }

    public boolean isEntryPortal(){
        return isEntry;
    }
}
