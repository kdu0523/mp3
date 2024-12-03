package fsft.fsftbuffer;

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

    private final Map<String, B> buffer;
    private final int capacity;
    private final Duration timeout;
    private final Map<String, Long> timestamps;

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
        this.timeout = timeout;
        this.buffer = new LinkedHashMap<String, B>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, B> eldest) {
                return size() > capacity;
            }
        };
        this.timestamps = new HashMap<>();
    }

    /**
     * Create a buffer with default capacity and timeout values.
     */
    public FSFTBuffer() {
        this(DEFAULT_CAPACITY, DEFAULT_TIMEOUT);
    }

    private void removeStaleEntries() {
        long currentTime = System.currentTimeMillis();
        timestamps.entrySet().removeIf(entry -> {
            if (currentTime - entry.getValue() > timeout.toMillis()) {
                buffer.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }

    /**
     * Add a value to the buffer.
     * If the buffer is full then remove the least recently accessed
     * object to make room for the new object.
     *
     * @param b the object to add to the buffer
     * @return true if the object was added successfully
     */
    public boolean put(B b) {
        if (b == null) {
            return false;
        }
        removeStaleEntries();
        String id = b.id();
        buffer.put(id, b);
        timestamps.put(id, System.currentTimeMillis());
        return true;
    }

    /**
     * @param id the identifier of the object to be retrieved
     * @return the object that matches the identifier from the buffer
     * @throws ObjectNotFoundException if the object is not found or has timed out
     */
    public B get(String id) throws ObjectNotFoundException {
        if (id == null) {
            throw new ObjectNotFoundException("Cannot get object with null id.");
        }
        removeStaleEntries();
        B obj = buffer.get(id);
        if (obj == null) {
            throw new ObjectNotFoundException("Object " + id + " not found in the buffer.");
        }
        touch(id);
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
        if (id == null || !buffer.containsKey(id)) {
            return false;
        }
        timestamps.put(id, System.currentTimeMillis());
        return true;
    }
}
