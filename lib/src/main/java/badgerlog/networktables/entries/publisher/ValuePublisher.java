package badgerlog.networktables.entries.publisher;

import badgerlog.Dashboard;
import badgerlog.entry.configuration.Configuration;
import badgerlog.networktables.mappings.Mapping;
import badgerlog.networktables.mappings.Mappings;
import edu.wpi.first.networktables.GenericPublisher;
import edu.wpi.first.networktables.NetworkTableType;

/**
 * {@link Publisher} implementing the basic publishing functions with a {@link Mapping}.
 *
 * @param <T> the starting type of the value. This does not have to be a valid {@link NetworkTableType} because if a Mapping exists for the starting type, then it will be converted to one
 */
public final class ValuePublisher<T> implements Publisher<T> {
    private final Configuration config;

    private final Mapping<T, Object> mapping;

    private final GenericPublisher publisher;

    /**
     * Default constructor for {@link ValuePublisher}
     *
     * @param key            the key for NetworkTables
     * @param fieldTypeClass the {@link Class} type of the value
     * @param config         the configuration for the {@link Mapping}
     * @see Mappings
     */
    public ValuePublisher(String key, Class<T> fieldTypeClass, Configuration config) {
        this.config = config;

        this.mapping = Mappings.findMapping(fieldTypeClass);

        NetworkTableType networkTableType = Mappings.findMappingType(fieldTypeClass);
        this.publisher = Dashboard.defaultTable.getEntry(key).getTopic().genericPublish(networkTableType.getValueStr());
    }

    @Override
    public void publishValue(T value) {
        publisher.setValue(mapping.toNT(value, config));
    }
}
