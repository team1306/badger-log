package badgerlog.networktables.entries;

import badgerlog.Dashboard;
import badgerlog.networktables.entries.publisher.Publisher;
import badgerlog.networktables.entries.subscriber.Subscriber;
import edu.wpi.first.networktables.StructEntry;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.util.struct.StructSerializable;

import javax.annotation.Nonnull;

/**
 * A NetworkTables entry handler for struct values, combining both {@link Publisher} and {@link Subscriber} functionality.
 * Uses a {@link StructEntry} to send values according to the specified struct. This is one of 
 * three available options for publishing structs to NetworkTables.
 *
 * @param <T> the struct type to handle, must implement {@link StructSerializable}
 */
public final class StructValueEntry<T> implements Subscriber<T>, Publisher<T> {

    private final StructEntry<T> entry;

    /**
     * Constructs a struct entry handler and initializes the NetworkTables entry with a default value.
     *
     * @param key          NetworkTables entry key
     * @param struct       Struct schema for serialization
     * @param defaultValue Initial value to publish
     */
    public StructValueEntry(@Nonnull String key, @Nonnull Struct<T> struct, @Nonnull T defaultValue) {
        entry = Dashboard.defaultTable.getStructTopic(key, struct).getEntry(defaultValue);
        publishValue(defaultValue);
    }

    @Override
    public void publishValue(T value) {
        entry.set(value);
    }

    @Override
    public T retrieveValue() {
        return entry.get();
    }
}
