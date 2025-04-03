package badgerlog.networktables.entries.publisher;

/**
 * Interface for a publisher putting values to NetworkTables
 *
 * @param <T> the starting type before mapping
 */
public interface Publisher<T> {
    /**
     * Publish a value to NetworkTables
     *
     * @param value the starting value to publish, may be converted to correspond to a NetworkTables type
     */
    void publishValue(T value);
}
