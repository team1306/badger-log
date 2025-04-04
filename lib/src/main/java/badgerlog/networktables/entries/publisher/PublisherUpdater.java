package badgerlog.networktables.entries.publisher;

import badgerlog.networktables.Updater;

import java.util.function.Supplier;

/// Updater class for a [Publisher]
///
/// @param <T> the starting type of the values
public final class PublisherUpdater<T> implements Updater {

    private final Publisher<T> publisher;
    private final Supplier<T> valueSupplier;

    /// Default constructor for [PublisherUpdater]
    ///
    /// @param publisher     the [Publisher] to use
    /// @param valueSupplier the [Supplier] for the value to be put to NetworkTables
    public PublisherUpdater(Publisher<T> publisher, Supplier<T> valueSupplier) {
        this.publisher = publisher;
        this.valueSupplier = valueSupplier;
    }

    @Override
    public void update() {
        publisher.publishValue(valueSupplier.get());
    }
}
