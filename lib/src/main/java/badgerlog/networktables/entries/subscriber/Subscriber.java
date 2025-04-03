package badgerlog.networktables.entries.subscriber;

/**
 * Interface for a subscriber getting values from NetworkTables
 *
 * @param <T> the starting type before mapping
 */
public interface Subscriber<T> {
    /**
     * Get a value from NetworkTables
     *
     * @return the value from NetworkTables, converted to the starting type
     */
    T retrieveValue();
}
