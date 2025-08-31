package badgerlog.networktables;

import java.util.function.Supplier;

/**
 * Wrapper around a {@link NTEntry} that implements {@link NTUpdatable} to allow for periodic updating the value on NetworkTables.
 *
 * @param <T> the type of the entry
 */
@SuppressWarnings("ClassCanBeRecord")
public final class PublisherNTUpdatable<T> implements NTUpdatable {

    private final NTEntry<T> publisher;
    private final Supplier<T> valueSupplier;

    /**
     * Constructs a new PublisherNTUpdatable. Does not publish the value from {@code valueSupplier} on start, only on {@link #update}.
     *
     * @param entry a NetworkTables entry to use for publishing
     * @param valueSupplier a supplier for the value on NetworkTables
     */
    public PublisherNTUpdatable(NTEntry<T> entry, Supplier<T> valueSupplier) {
        this.publisher = entry;
        this.valueSupplier = valueSupplier;
    }

    @Override
    public void update() {
        publisher.publishValue(valueSupplier.get());
    }
}
