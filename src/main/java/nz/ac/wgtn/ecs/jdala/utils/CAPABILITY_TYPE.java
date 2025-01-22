package nz.ac.wgtn.ecs.jdala.utils;

public enum CAPABILITY_TYPE {
    IMMUTABLE,
    ISOLATED,
    LOCAL,
    UNSAFE;

    @Override
    public String toString() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}
