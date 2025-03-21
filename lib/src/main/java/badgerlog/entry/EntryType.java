package badgerlog.entry;

/**
 * The type of NetworkTable entry 
 */
public enum EntryType {
    /**
     * Publish a value to NetworkTables from the field value
     */
    Publisher,
    /**
     * Subscribe (get) a value from NetworkTables and update the field value
     */
    Subscriber,
    /**
     * Sendable is an interface for use to put and get values in a more concise way
     */
    Sendable
}
