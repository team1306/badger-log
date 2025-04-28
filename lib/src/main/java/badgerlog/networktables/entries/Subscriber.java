package badgerlog.networktables.entries;

/**
 * Defines a subscriber interface for retrieving typed values from NetworkTables.
 * Implementations handle type conversion from a valid NetworkTable types to starting types.
 *
 * @param <T> The source data type before conversion
 */
public interface Subscriber<T> {
    /**
     * Fetches and converts the current value from NetworkTables.
     *
     * @return The latest value from NetworkTables, converted to type {@code T}
     */
    T retrieveValue();
}
