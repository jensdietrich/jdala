package nz.ac.wgtn.ecs.jdala.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation used for classifying an object as local.
 * Local objects can:
 * - only be aliased in their own thread
 * - can not be dereferenced by any other thread,
 * - giving only the thread that created the local object access to its fields and methods
 *
 * @author Quinten Smit
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.LOCAL_VARIABLE, ElementType.TYPE_USE})
public @interface Local {
}
