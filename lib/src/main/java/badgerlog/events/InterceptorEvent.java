package badgerlog.events;

import java.util.function.Function;

public record InterceptorEvent<T>(Class<T> type, Function<T, T> valueConsumer) {
    public T invoke(T value){
        return valueConsumer.apply(value);
    }
    
    public boolean matches(Class<?> type){
        return type.isAssignableFrom(this.type);
    }
}
