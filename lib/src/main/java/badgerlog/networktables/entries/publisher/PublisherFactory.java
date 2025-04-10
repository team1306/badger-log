package badgerlog.networktables.entries.publisher;

import badgerlog.Dashboard;
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
     * @param key             the key for the publisher on NetworkTables
     * @param value           the initial value for the publisher
     * @param config          the configuration for value entries
     * @param structOption   the specific options to use for struct publishing. Defaults to whatever is in the config provided
     * @param <T>             the starting type before mapping
     * @return a {@link Publisher} with the correct initialization
     * @see ValuePublisher
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows({IllegalAccessException.class, NoSuchFieldException.class})
    public static <T> Publisher<T> getPublisherFromValue(String key, T value, String config, DashboardConfig.StructOptions structOption) {
        Class<T> valueTypeClass = (Class<T>) value.getClass();

        if (StructSerializable.class.isAssignableFrom(valueTypeClass)) {
            Struct<T> struct = (Struct<T>) valueTypeClass.getField("struct").get(null);
            DashboardConfig.StructOptions option = structOption == DashboardConfig.StructOptions.DEFAULT ? Dashboard.config.getStructOptions() : structOption;
            
            return switch (option) {
                case STRUCT, DEFAULT -> new StructValueEntry<>(key, struct, value);
                case SUB_TABLE -> new SubtablePublisher<>(key, struct);
                case MAPPING -> new ValuePublisher<>(key, valueTypeClass, config);
            };
        }

        return new ValuePublisher<>(key, valueTypeClass, config);
    }
}
