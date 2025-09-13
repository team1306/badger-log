package badgerlog.annotations;

/**
 * Represents all the possible options for entries on NetworkTables.
 */
public enum EntryType {
    /**
     * Sends a value to NetworkTables
     */
    PUBLISHER,
    /**
     * Gets a value from NetworkTables
     */
    SUBSCRIBER,
    /**
     * Puts a Sendable value to NetworkTables
     */
    SENDABLE,
    INTELLIGENT
}
