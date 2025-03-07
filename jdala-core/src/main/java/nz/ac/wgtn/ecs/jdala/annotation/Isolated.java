package nz.ac.wgtn.ecs.jdala.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for classifying an object as isolated
 * This object can only have a single reference which cannot be aliased but can be transferred between threads
 *
 * @author Quinten Smit
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface Isolated {
}
