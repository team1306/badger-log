package badgerlog.networktables.entries.subscriber;

import badgerlog.Dashboard;
import badgerlog.networktables.entries.publisher.Publisher;
import badgerlog.networktables.mappings.Mapping;
import badgerlog.networktables.mappings.Mappings;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableType;

/**
 * {@link Subscriber} implementing the basic subscribing functions with a {@link Mapping}. It is also a {@link Publisher} to make it easier to get the default value and find the key easier.
 *
 * @param <T> the starting type of the value. This does not have to be a valid {@link NetworkTableType} because if a Mapping exists for the starting type, then it will be converted to one
 */
public final class ValueSubscriber<T> implements Subscriber<T>, Publisher<T> {

    private final String config;

    private final Mapping<T, Object> fieldValueMapping;
    private final GenericEntry entry;

    /**
     * Default constructor for {@link ValueSubscriber}
     *
     * @param key            the key for NetworkTables
     * @param fieldTypeClass the {@link Class} type of the value
     * @param initialValue   the initial value for NetworkTables
     * @param config         the configuration for the {@link Mapping}
     * @see Mappings
     */
    public ValueSubscriber(String key, Class<T> fieldTypeClass, T initialValue, String config) {
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
        return fieldValueMapping.toField(entry.get().getValue(), config);
    }
}
