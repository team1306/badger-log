package badgerlog.networktables;

import java.util.function.Consumer;

@SuppressWarnings("ClassCanBeRecord")
public final class SubscriberUpdater<T> implements Updater, NT {

    private final NTEntry<T> subscriber;
    private final Consumer<T> valueConsumer;

    public SubscriberUpdater(NTEntry<T> subscriber, Consumer<T> valueConsumer) {
        this.subscriber = subscriber;
        this.valueConsumer = valueConsumer;
    }

    @Override
    public void update() {
        valueConsumer.accept(subscriber.retrieveValue());
    }
}
