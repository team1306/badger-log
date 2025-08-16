package badgerlog.networktables;

import java.util.function.Consumer;

/**
 * Automates periodic consumption of NetworkTables values by linking a {@link Subscriber} with a value {@link Consumer}.
 * Implements the {@link Updater} interface to enable scheduled updates, typically used in loops or timed tasks.
 *
 * @param <T> Type of data being consumed
 */
public final class SubscriberUpdater<T> implements Updater {

    private final NTEntry<T> subscriber;
    private final Consumer<T> valueConsumer;

    /**
     * Constructs an updater that bridges a subscriber and a value processor.
     *
     * @param subscriber    Source of NetworkTables values
     * @param valueConsumer Receiver for retrieved values
     */
    public SubscriberUpdater(NTEntry<T> subscriber, Consumer<T> valueConsumer) {
        this.subscriber = subscriber;
        this.valueConsumer = valueConsumer;
    }

    @Override
    public void update() {
        valueConsumer.accept(subscriber.retrieveValue());
    }
}
