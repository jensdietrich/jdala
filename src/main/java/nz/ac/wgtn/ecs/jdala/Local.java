package nz.ac.wgtn.ecs.jdala;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.LOCAL_VARIABLE)
// can only be aliased in their own thread,
// it can not be dereferenced by any other thread,
// giving only the thread that created the local object access to its fields and methods
public @interface Local {
}
