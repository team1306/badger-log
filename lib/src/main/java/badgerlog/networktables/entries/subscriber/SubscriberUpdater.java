package badgerlog.networktables.entries.subscriber;

import badgerlog.networktables.Updater;

import java.util.function.Consumer;

/**
 * Updater class for a {@link Subscriber}
 *
 * @param <T> the starting type of the values
 */
public final class SubscriberUpdater<T> implements Updater {

    private final Subscriber<T> subscriber;
    private final Consumer<T> valueConsumer;

    /**
     * Default constructor for {@link SubscriberUpdater}
     *
     * @param subscriber    the {@link Subscriber} to use
     * @param valueConsumer the {@link Consumer} for the value from NetworkTables to be set to
     */
    public SubscriberUpdater(Subscriber<T> subscriber, Consumer<T> valueConsumer) {
        this.subscriber = subscriber;
        this.valueConsumer = valueConsumer;
    }

    @Override
    public void update() {
        valueConsumer.accept(subscriber.retrieveValue());
    }
}
