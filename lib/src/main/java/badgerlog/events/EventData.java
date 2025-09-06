package badgerlog.events;

public record EventData<T>(String key, double timestamp, T oldValue, T newValue) {
}
