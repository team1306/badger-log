package badgerlog.networktables.entries;

import badgerlog.Dashboard;
import badgerlog.networktables.entries.publisher.Publisher;
import badgerlog.networktables.entries.subscriber.Subscriber;
import edu.wpi.first.networktables.StructEntry;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.util.struct.StructSerializable;

/**
 * {@link Subscriber} and {@link Publisher} implementing the other alternative method of putting a {@link Struct} to NetworkTables
 *
 * @param <T> the Struct type. Must be of type {@link StructSerializable}
 */
public final class StructValueEntry<T> implements Subscriber<T>, Publisher<T> {

    private final StructEntry<T> entry;

    /**
     * Default constructor for {@link StructValueEntry}
     *
     * @param key          the key for NetworkTables
     * @param struct       the {@link Struct} to use for the schema on NetworkTables
     * @param defaultValue the default value for NetworkTables
     */
    public StructValueEntry(String key, Struct<T> struct, T defaultValue) {
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
