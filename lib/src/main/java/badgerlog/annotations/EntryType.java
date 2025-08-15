package badgerlog.annotations;

/**
 * Specifies the type of interaction for NetworkTables entries.
 */
public enum EntryType {
    /**
     * Publishes a field value to NetworkTables.
     */
    Publisher,

    /**
     * Subscribes to a NetworkTables value and updates the field.
     */
    Subscriber,

    /**
     * Uses the Sendable interface for simplified value exchange.
     */
    Sendable
}
