package badgerlog.annotations;

/**
 * Represents all the possible options for entries on NetworkTables.
 */
public enum EntryType {
    /**
     * Sends a value to NetworkTables
     */
    Publisher,
    /**
     * Gets a value from NetworkTables
     */
    Subscriber,
    /**
     * Puts a Sendable value to NetworkTables
     */
    Sendable
}
