package badgerlog.events;

import java.util.function.Consumer;

public record WatcherEvent<T>(Class<T> type, Consumer<T> valueConsumer) {
    public void invoke(T value){
        valueConsumer.accept(value);
    }
}
