package fsft.fsftbuffer;

import java.time.Duration;
import java.util.*;

import java.time.Duration;
import java.util.*;


/**
 * FSFTBuffer is a cache of objects that time out after a certain period of time.
 * The buffer has a fixed capacity and uses a least recently used (LRU) eviction
 * policy. Objects in the buffer that have not been refreshed within the timeout
 * period are removed from the cache.
 *
 * @param <B> the type of the objects in the buffer
 */
public class FSFTBuffer<B extends Bufferable> {

    /* the default buffer size is 32 objects */
    public static final int DEFAULT_CAPACITY = 32;

    /* the default timeout value is 180 seconds */
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(180);

    /* TODO: Implement this datatype */
    private final LinkedHashMap<String, B> buffer = new LinkedHashMap<>();
    private final int capacity;

    /**
     * Create a buffer with a fixed capacity and a timeout value.
     *
     * @param capacity the number of objects the buffer can hold
     * @param timeout  the duration, in seconds, an object should
     *                 be in the buffer before it times out
     */
    public FSFTBuffer(int capacity, Duration timeout) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be a positive integer");
        }
        if (timeout.isNegative()) {
            throw new IllegalArgumentException("Timeout must be a positive duration");
        }
        this.capacity = capacity;
    }

    /**
     * Create a buffer with default capacity and timeout values.
     */
    public FSFTBuffer() {
        this(DEFAULT_CAPACITY, DEFAULT_TIMEOUT);
    }

    /**
     * Add a value to the buffer.
     * If the buffer is full then remove the least recently accessed
     * object to make room for the new object.
     * This method can be used to replace an object in the buffer with
     * a newer instance. {@code b} is uniquely identified by its id,
     * {@code b.id()}.
     */
    public boolean put(B b) {
        String id = b.id();

        if (buffer.containsKey(id)) {
            buffer.remove(id);
        }

        buffer.put(id, b);

        if (buffer.size() > capacity) {
            buffer.remove(buffer.keySet().iterator().next());
        }

        return true;
    }


    /**
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the
     * buffer
     */
    public B get(String id) throws ObjectNotFoundException {
        B obj = buffer.get(id);
        if (obj == null) {
            throw new ObjectNotFoundException("Object " + id + " not found in the buffer.");
        }
        return obj;
    }

    /**
     * Update the last refresh time for the object with the provided id.
     * This method is used to mark an object as "not stale" so that its
     * timeout is delayed.
     *
     * @param id the identifier of the object to "touch"
     * @return true if successful and false otherwise
     */
    public boolean touch(String id) {
        if (buffer.containsKey(id)) {
            B value = buffer.remove(id);
            buffer.put(id, value);
            return true;
        }
        return false;
    }

}
