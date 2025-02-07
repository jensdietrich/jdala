package nz.ac.wgtn.ecs.jdala.utils;


/**
 * Used to store data about where an annotation is
 */
public class AnnotationPair {
    public int index;
    public CAPABILITY_TYPE annotation;
    public String name;
    public String location;

    public AnnotationPair(int index, CAPABILITY_TYPE annotation, String location, String name) {
        this.index = index;
        this.annotation = annotation;
        this.location = location;
        this.name = name;
    }

    public AnnotationPair(int index, CAPABILITY_TYPE annotation, String location) {
        this.index = index;
        this.annotation = annotation;
        this.location = location;
    }
}
