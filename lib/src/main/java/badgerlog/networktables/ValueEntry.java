package badgerlog.networktables;

import badgerlog.Dashboard;
import badgerlog.annotations.configuration.Configuration;
import badgerlog.conversion.Mapping;
import badgerlog.conversion.Mappings;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableType;

/**
 * Wraps a {@link GenericEntry}, and allows for the use of the Mapping system.
 *
 * @param <T> the type to use. Does not need to be a valid NetworkTableType
 */
public final class ValueEntry<T> implements NTEntry<T> {

    private final Configuration config;

    private final Mapping<T, Object> fieldValueMapping;
    private final GenericEntry entry;
    private final String key;

    /**
     * Constructs a new ValueEntry, creating the entry on NetworkTables, and finding the {@link Mapping} for the
     * specified {@code valueClass}.
     *
     * <p>This initially publishes the {@code initialValue} to make the entry appear on NetworkTables.</p>
     *
     * @param key the key on NetworkTables
     * @param valueClass the class type of the {@code initialValue}
     * @param initialValue the value to initially publish to NetworkTables
     * @param config the configuration to use for the Mapping
     */
    public ValueEntry(String key, Class<T> valueClass, T initialValue, Configuration config) {
        this.config = config;
        this.key = key;

        this.fieldValueMapping = Mappings.findMapping(valueClass);

        NetworkTableType networkTableType = Mappings.findMappingType(valueClass);
        this.entry = Dashboard.defaultTable.getEntry(key).getTopic().getGenericEntry(networkTableType.getValueStr());

        publishValue(initialValue);
    }

    @Override
    public void publishValue(T value) {
        entry.setValue(fieldValueMapping.toNT(value, config));
    }

    @Override
    public T retrieveValue() {
        return fieldValueMapping.toStart(entry.get().getValue(), config);
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
