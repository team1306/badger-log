package badgerlog.networktables;

import badgerlog.Dashboard;
import edu.wpi.first.networktables.StructEntry;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.util.struct.StructSerializable;

/**
 * A NetworkTables entry handler for struct values, combining both functionality.
 * Uses a {@link StructEntry} to send values according to the specified struct. This is one of
 * three available options for publishing structs to NetworkTables.
 *
 * @param <T> the struct type to handle, must implement {@link StructSerializable}
 */
public final class StructValueEntry<T> implements NTEntry<T> {

    private final StructEntry<T> entry;
    private final String key;

    /**
     * Constructs a struct entry handler and initializes the NetworkTables entry with a default value.
     *
     * @param key          NetworkTables entry key
     * @param struct       Struct schema for serialization
     * @param defaultValue Initial value to publish
     */
    public StructValueEntry(String key, Struct<T> struct, T defaultValue) {
        this.key = key;
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
