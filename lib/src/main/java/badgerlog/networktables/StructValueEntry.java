package badgerlog.networktables;

import badgerlog.Dashboard;
import edu.wpi.first.networktables.StructEntry;
import edu.wpi.first.util.struct.Struct;

/**
 * A wrapper for the {@link StructEntry} on NetworkTables.
 * An implementation of the {@code StructType.STRUCT} for NetworkTables.
 *
 * @param <T> the type to use. Does not need to be a valid NetworkTableType
 */
public final class StructValueEntry<T> implements NTEntry<T> {

    private final StructEntry<T> entry;
    private final String key;

    /**
     * Constructs a new StructValueEntry and creates the entry on NetworkTables.
     *
     * @param key the key on NetworkTables
     * @param struct the struct to use for the NetworkTables entry
     * @param initialValue the initial value to be published to NetworkTables
     */
    public StructValueEntry(String key, Struct<T> struct, T initialValue) {
        this.key = key;
        entry = Dashboard.defaultTable.getStructTopic(key, struct).getEntry(initialValue);
        publishValue(initialValue);
    }

    @Override
    public void publishValue(T value) {
        entry.set(value);
    }

    @Override
    public T retrieveValue() {
        return entry.get();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void close() {
        entry.unpublish();
        entry.close();
    }
}
