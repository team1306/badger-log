package badgerlog.networktables;

import java.util.function.Consumer;

/**
 * Wrapper around a {@link NTEntry} that implements {@link NTUpdatable} to allow for periodic updating using the value
 * from NetworkTables.
 *
 * @param <T> the type of the entry
 */
@SuppressWarnings("ClassCanBeRecord")
@Deprecated
public final class SubscriberNTUpdatable<T> implements NTUpdatable {

    private final NTEntry<T> subscriber;
    private final Consumer<T> valueConsumer;

    /**
     * Constructs a new SubscriberNTUpdatable. Does not call the {@code valueConsumer} on start, only on
     * {@link #update}.
     *
     * @param entry a NetworkTables entry to use for subscribing
     * @param valueConsumer a consumer that takes the value on NetworkTables
     */
    public SubscriberNTUpdatable(NTEntry<T> entry, Consumer<T> valueConsumer) {
        this.subscriber = entry;
        this.valueConsumer = valueConsumer;
    }

    @Override
    public void update() {
        valueConsumer.accept(subscriber.retrieveValue());
    }
}
