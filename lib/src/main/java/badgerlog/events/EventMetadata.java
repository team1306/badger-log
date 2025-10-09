package badgerlog.events;

import badgerlog.annotations.EventType;

public record EventMetadata(String[] keys, String name, EventType type) {
}
