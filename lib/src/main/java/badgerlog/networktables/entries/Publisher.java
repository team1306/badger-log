package badgerlog.networktables.entries;

/**
 * Defines a publisher interface for sending typed values to NetworkTables.
 * Implementations handle type conversion from a starting type to a valid NetworkTable type.
 *
 * @param <T> The source data type before conversion
 */
public interface Publisher<T> {
    /**
     * Publishes a value to NetworkTables, performing necessary type conversions.
     *
     * @param value The value to send, in its original type {@code T}
     */
    void publishValue(T value);
}
