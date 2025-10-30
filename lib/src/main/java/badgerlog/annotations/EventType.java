package badgerlog.annotations;

/**
 * Represents all possible types of event firing conditions
 */
public enum EventType {
    /**
     * Fires an event when a value comes from a non-local source
     */
    INCOMING,
    /**
     * Fires an event when a value changes locally
     */
    OUTGOING,
    /**
     * Fires an event when a value changes, regardless of change location
     */
    ALL
}
