package JavaConcurrencyInPractice.net.jcip.examples;

import JavaConcurrencyInPractice.net.jcip.annotations.*;

/**
 * SafeLazyInitialization
 * <p/>
 * Thread-safe lazy initialization
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class SafeLazyInitialization {
    private static Resource resource;

    public synchronized static Resource getInstance() {
        if (resource == null)
            resource = new Resource();
        return resource;
    }

    static class Resource {
    }
}
