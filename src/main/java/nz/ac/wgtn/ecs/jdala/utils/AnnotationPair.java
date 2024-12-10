package nz.ac.wgtn.ecs.jdala.utils;

import java.lang.annotation.Annotation;

public class AnnotationPair {
    public static enum ANNOTATION_TYPE {
        IMMUTABLE,
        ISOLATED,
        LOCAL,
        UNSAFE;

        @Override
        public String toString() {
            return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
        }
    }

    public int index;
    public ANNOTATION_TYPE annotation;
    public String name;
    public String location;

    public AnnotationPair(int index, ANNOTATION_TYPE annotation, String location, String name) {
        this.index = index;
        this.annotation = annotation;
        this.location = location;
        this.name = name;
    }

    public AnnotationPair(int index, ANNOTATION_TYPE annotation, String location) {
        this.index = index;
        this.annotation = annotation;
        this.location = location;
    }
}
