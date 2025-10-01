package badgerlog.events;

import java.util.function.Function;

public record InterceptorEvent<T>(Class<T> type, Function<EventData<T>, T> valueConsumer) implements NTEvent {
    public T invoke(EventData<T> value) {
        return valueConsumer.apply(value);
    }

    public boolean matches(Class<?> type) {
        return type.isAssignableFrom(this.type);
    }
}
