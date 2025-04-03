package badgerlog.networktables.entries.publisher;

import badgerlog.DashboardConfig;
import badgerlog.networktables.entries.StructValueEntry;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.util.struct.StructSerializable;
import lombok.SneakyThrows;

/**
 * Factory class for all implementations of {@link Publisher}
 */
public final class PublisherFactory {
    private PublisherFactory() {
    }

    /**
     * Factory method to create a {@link Publisher} from existing mappings and implementations of Publisher
     *
     * @param dashboardConfig the configuration for BadgerLog
     * @param key             the key for the publisher on NetworkTables
     * @param value           the initial value for the publisher
     * @param config          the configuration for value entries
     * @param <T>             the starting type before mapping
     * @return a {@link Publisher} with the correct initialization
     * @see ValuePublisher
     */
    @SneakyThrows({IllegalAccessException.class, NoSuchFieldException.class})
    public static <T> Publisher<?> getPublisherFromValue(DashboardConfig dashboardConfig, String key, T value, String config) {
        Class<?> valueTypeClass = value.getClass();

        if (StructSerializable.class.isAssignableFrom(valueTypeClass)) {
            @SuppressWarnings("unchecked")
            Struct<T> struct = (Struct<T>) valueTypeClass.getField("struct").get(null);

            return switch (dashboardConfig.getStructOptions()) {
                case STRUCT -> new StructValueEntry<>(key, struct, value);
                case SUB_TABLE -> new SubtablePublisher<>(key, struct);
                case MAPPING -> new ValuePublisher<>(key, valueTypeClass, config);
            };
        }

        return new ValuePublisher<>(key, valueTypeClass, config);
    }
}
