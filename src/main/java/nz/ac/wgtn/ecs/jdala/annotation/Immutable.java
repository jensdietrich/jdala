package nz.ac.wgtn.ecs.jdala.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for classifying an object as immutable
 * This means the object can be freely shared but not mutated.
 *
 * @author Quinten Smit
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface Immutable {
}
