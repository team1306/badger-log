package badgerlog.networktables;

import badgerlog.BadgerLog;
import badgerlog.transformations.Mapping;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableType;

/**
 * Wraps a {@link GenericEntry}, and allows for the use of the Mapping system.
 *
 * @param <T> the type to use. Does not need to be a valid NetworkTableType
 */
public final class ValueEntry<T> implements NTEntry<T> {
    
    private final Mapping<T, Object> mapping;
    private final GenericEntry entry;
    private final String key;
    private final Class<?> type;

    /**
     * Constructs a new ValueEntry, creating the entry on NetworkTables, and finding the {@link Mapping} for the
     * specified {@code valueClass}.
     *
     * <p>This initially publishes the {@code initialValue} to make the entry appear on NetworkTables.</p>
     *
     * @param key the key on NetworkTables
     * @param valueClass the class type of the {@code initialValue}
     * @param initialValue the value to initially publish to NetworkTables
     */
    @SuppressWarnings("unchecked")
    public ValueEntry(String key, Class<T> valueClass, T initialValue, Mapping<T, ?> mapping, NetworkTableType networkTableType) {
        this.key = key;
        this.type = valueClass;
        this.mapping = (Mapping<T, Object>) mapping;
        
        this.entry = BadgerLog.defaultTable.getEntry(key).getTopic().getGenericEntry(networkTableType.getValueStr());

        publishValue(initialValue);
    }

    @Override
    public void publishValue(T value) {
        entry.setValue(mapping.evaluateForwards(value));
    }

    @Override
    public T retrieveValue() {
        return mapping.evaluateBackwards(entry.get().getValue());
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public void close() {
        entry.unpublish();
        entry.close();
    }
}
