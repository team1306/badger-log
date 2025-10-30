package badgerlog.events;

import badgerlog.annotations.EventType;

/**
 * Represents metadata about an event
 * @param keys the keys the event watches
 * @param name the name of the event
 * @param type the {@link EventType} of the event
 */
@SuppressWarnings("ArrayRecordComponent") // Should not be used like a record
public record EventMetadata(String[] keys, String name, EventType type) {
}
