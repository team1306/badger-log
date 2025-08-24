package badgerlog.networktables;

import badgerlog.Dashboard;
import edu.wpi.first.networktables.StructEntry;
import edu.wpi.first.util.struct.Struct;

public final class StructValueEntry<T> implements NTEntry<T> {

    private final StructEntry<T> entry;
    private final String key;

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
