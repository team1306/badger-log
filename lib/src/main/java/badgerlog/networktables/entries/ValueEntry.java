package badgerlog.networktables.entries;

import badgerlog.Dashboard;
import badgerlog.entry.configuration.Configuration;
import badgerlog.networktables.entries.publisher.Publisher;
import badgerlog.networktables.entries.subscriber.Subscriber;
import badgerlog.networktables.mappings.Mapping;
import badgerlog.networktables.mappings.Mappings;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableType;

import javax.annotation.Nonnull;

/**
 * Dual-purpose subscriber/publisher for generic values using type mappings. Third struct handling method, combining
 * publication and subscription for value management. Handles type conversions through the {@link Mapping} system.
 *
 * @param <T> Source data type before conversion via {@link Mapping}
 */
public final class ValueEntry<T> implements Subscriber<T>, Publisher<T> {

    private final Configuration config;

    private final Mapping<T, Object> fieldValueMapping;
    private final GenericEntry entry;

    /**
     * Creates a subscriber/publisher pair with initial value.
     *
     * @param key            NetworkTables entry key
     * @param fieldTypeClass Starting class of the source type
     * @param initialValue   Default value to initialize NetworkTable entry
     * @param config         Configuration for mapping behavior
     */
    public ValueEntry(@Nonnull String key, @Nonnull Class<T> fieldTypeClass, @Nonnull T initialValue, @Nonnull Configuration config) {
        this.config = config;

        this.fieldValueMapping = Mappings.findMapping(fieldTypeClass);

        NetworkTableType networkTableType = Mappings.findMappingType(fieldTypeClass);
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
}
