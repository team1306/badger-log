package badgerlog.events;

import badgerlog.annotations.EventType;

public record EventMetadata(String key, String name, EventType type) {
}
