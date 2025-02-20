package nz.ac.wgtn.ecs.jdala.exceptions;

/**
 * Thrown when there is a violation of a set Dala Capability
 * e.g. Local is used outside local thread
 *
 * @author Quinten Smit
 */
public class DalaCapabilityViolationException extends RuntimeException{
    public DalaCapabilityViolationException(String message) {
        super(message);
    }
}
