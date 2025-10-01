package badgerlog.events;

import java.util.function.Consumer;

public record WatcherEvent<T>(Class<T> type, Consumer<EventData<T>> valueConsumer) implements NTEvent {
    public void invoke(EventData<T> value) {
        valueConsumer.accept(value);
    }

    public boolean matches(Class<?> type) {
        return type.isAssignableFrom(this.type);
    }
}
