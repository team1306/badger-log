package badgerlog.events;

import java.util.function.Function;

public record InterceptorEvent<T>(Class<T> type, Function<EventData<T>, T> valueConsumer) {
    public boolean matches(Class<?> type) {
        System.out.println(type.getSimpleName() + "-" + this.type.getSimpleName());
        return type.isAssignableFrom(this.type);
    }
}
