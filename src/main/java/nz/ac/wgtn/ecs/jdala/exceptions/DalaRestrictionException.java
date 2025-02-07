package nz.ac.wgtn.ecs.jdala.exceptions;

/**
 * Thrown when there is an attempt to either cast an object to an object with more capabilities than itself
 * e.g Immutable -> Local
 * or an object refers to another object with more capabilities than itself
 */
public class DalaRestrictionException extends RuntimeException {
    public DalaRestrictionException(String message) {
        super(message);
    }

}
