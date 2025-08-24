package badgerlog.networktables;

import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public final class PublisherUpdater<T> implements Updater, NT {

    private final NTEntry<T> publisher;
    private final Supplier<T> valueSupplier;

    public PublisherUpdater(NTEntry<T> publisher, Supplier<T> valueSupplier) {
        this.publisher = publisher;
        this.valueSupplier = valueSupplier;
    }

    @Override
    public void update() {
        publisher.publishValue(valueSupplier.get());
    }
}
