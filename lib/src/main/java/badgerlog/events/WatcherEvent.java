package badgerlog.events;

import java.util.function.Consumer;

/**
 * Represents an event with a callback and type
 * @param type the class type of the event
 * @param valueConsumer a function consuming an event
 * @param <T> the type of the event
 */
public record WatcherEvent<T>(Class<T> type, Consumer<EventData<T>> valueConsumer) {
    /**
     * Invokes the {@code valueConsumer} with the {@code value}
     * @param value the value to invoke the event with
     */
    public void invoke(EventData<T> value) {
        valueConsumer.accept(value);
    }

    /**
     * {@return if the type matches the type of this event}
     */
    public boolean matches(Class<?> type) {
        return type.isAssignableFrom(this.type);
    }
}
