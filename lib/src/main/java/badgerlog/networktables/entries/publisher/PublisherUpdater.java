package badgerlog.networktables.entries.publisher;

import badgerlog.networktables.entries.Updater;

import java.util.function.Supplier;

/**
 * Automates periodic value publication to NetworkTables by linking a {@link Publisher} with a value {@link Supplier}.
 * Implements the {@link Updater} interface to enable scheduled updates, typically used in loops or timed tasks.
 *
 * @param <T> Type of data being published
 */
public final class PublisherUpdater<T> implements Updater {

    private final Publisher<T> publisher;
    private final Supplier<T> valueSupplier;

    /**
     * Constructs an updater that bridges a publisher and a dynamic value source.
     *
     * @param publisher     Target for value publishing
     * @param valueSupplier Provider of current values to publish
     */
    public PublisherUpdater(Publisher<T> publisher, Supplier<T> valueSupplier) {
        this.publisher = publisher;
        this.valueSupplier = valueSupplier;
    }

    @Override
    public void update() {
        publisher.publishValue(valueSupplier.get());
    }
}
