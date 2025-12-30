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
     * Sends a value to NetworkTables when a field is set, and get a value form NetworkTables when a field is retrieved
     */
    INTELLIGENT
}
