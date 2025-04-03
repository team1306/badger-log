package badgerlog.networktables.entries.subscriber;

import badgerlog.DashboardConfig;
import badgerlog.networktables.entries.StructValueEntry;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.util.struct.StructSerializable;
import lombok.SneakyThrows;

/**
 * Factory class for all implementations of {@link Subscriber}
 */
public final class SubscriberFactory {
    private SubscriberFactory() {
    }

    /**
     * Factory method to create a {@link Subscriber} from existing mappings and implementations of Subscriber
     *
     * @param dashboardConfig the configuration for BadgerLog
     * @param key             the key for the subscriber on NetworkTables
     * @param value           the initial value for the subscriber--gets put to NetworkTables as the default value
     * @param config          the configuration for value entries
     * @param <T>             the starting type before mapping
     * @return a Subscriber with the correct initialization
     * @see ValueSubscriber
     */
    @SneakyThrows({IllegalAccessException.class, NoSuchFieldException.class})
    public static <T> Subscriber<?> getSubscriberFromValue(DashboardConfig dashboardConfig, String key, T value, String config) {
        @SuppressWarnings("unchecked")
        Class<T> valueTypeClass = (Class<T>) value.getClass();

        if (StructSerializable.class.isAssignableFrom(valueTypeClass)) {
            @SuppressWarnings("unchecked")
            Struct<T> struct = (Struct<T>) valueTypeClass.getField("struct").get(null);

            return switch (dashboardConfig.getStructOptions()) {
                case STRUCT -> new StructValueEntry<>(key, struct, value);
                case SUB_TABLE -> new SubtableSubscriber<>(key, struct, value);
                case MAPPING -> new ValueSubscriber<>(key, valueTypeClass, value, config);
            };
        }

        return new ValueSubscriber<>(key, valueTypeClass, value, config);
    }
}
