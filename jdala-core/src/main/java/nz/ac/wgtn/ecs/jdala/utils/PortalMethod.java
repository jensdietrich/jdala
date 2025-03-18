package nz.ac.wgtn.ecs.jdala.utils;

public class PortalMethod {
    private final String methodName;
    private final int parameterIndex;
    private final String descriptor ;

    public PortalMethod(String methodName, int parameterIndex, String descriptor) {
        this.methodName = methodName;
        this.parameterIndex = parameterIndex;
        if (descriptor == null) descriptor = "";
        this.descriptor = descriptor;
    }

    public PortalMethod(String methodName, String descriptor) {
        this(methodName, -1, descriptor);
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
        return parameterIndex >= 0;
    }
}
