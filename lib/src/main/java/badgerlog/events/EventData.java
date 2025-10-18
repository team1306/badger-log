package badgerlog.events;

public record EventData<T>(String key, double timestamp, T newValue) {
}
