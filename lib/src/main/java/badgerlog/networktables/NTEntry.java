package badgerlog.networktables;

/**
 * Interface for wrappers of NetworkTables entries.
 *
 * @param <T> the type to put on NetworkTables
 */
@SuppressWarnings("unused")
public interface NTEntry<T> extends AutoCloseable, NT {
    /**
     * Puts a value to NetworkTables
     *
     * @param value the value to put to NetworkTables
     */
    void publishValue(T value);

    /**
     * Gets the last value retrieved from NetworkTables
     *
     * @return the last value on NetworkTables
     */
    T retrieveValue();

    /**
     * Gets the key used by the entry on NetworkTables
     *
     * @return the key on NetworkTables
     */
    String getKey();
}
