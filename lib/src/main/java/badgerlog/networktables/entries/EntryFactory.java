package badgerlog.networktables.entries;

import badgerlog.Dashboard;
import badgerlog.StructOptions;
import badgerlog.entry.configuration.Configuration;
import badgerlog.networktables.entries.publisher.Publisher;
import badgerlog.networktables.entries.subscriber.Subscriber;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.util.struct.StructSerializable;
import lombok.SneakyThrows;

/**
 * Factory class for creating NetworkTables {@link Publisher} and {@link Subscriber} entries based on value types
 * and configuration. Automatically selects the appropriate entry implementation (struct, subtable, or mapped value)
 * according to the {@link StructOptions} specified in the configuration.
 *
 * <p>Handles both struct-based data (via {@link StructSerializable}) and generic values with type mappings.
 */
@SuppressWarnings("DuplicatedCode") // TODO work on structure later
public class EntryFactory {
    /**
     * Creates a {@link Publisher} optimized for the value type and configuration.
     * Implementation selection logic:
     * <ul>
     *   <li>For {@link StructSerializable} values: Uses {@link StructValueEntry}, {@link SubtableEntry},
     *       or {@link ValueEntry} based on {@link StructOptions}</li>
     *   <li>For generic values: Defaults to {@link ValueEntry} with type mapping</li>
     * </ul>
     *
     * @param key    NetworkTables entry key
     * @param value  Initial value (determines type handling)
     * @param config Configuration specifying struct preferences and mappings
     * @param <T>    Data type of the value
     * @return Publisher implementation best suited for the value type
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows({IllegalAccessException.class, NoSuchFieldException.class})
    public static <T> Publisher<T> createPublisherFromValue(String key, T value, Configuration config) {
        Class<T> valueTypeClass = (Class<T>) value.getClass();

        if (StructSerializable.class.isAssignableFrom(valueTypeClass)) {
            Struct<T> struct = (Struct<T>) valueTypeClass.getField("struct").get(null);
            StructOptions option = config.getStructOptions() == null ? Dashboard.config.getStructOptions() : config.getStructOptions();

            return switch (option) {
                case STRUCT -> new StructValueEntry<>(key, struct, value);
                case SUB_TABLE -> new SubtableEntry<>(key, struct, value);
                case MAPPING -> new ValueEntry<>(key, valueTypeClass, value, config);
            };
        }

        return new ValueEntry<>(key, valueTypeClass, value, config);
    }

    /**
     * Creates a {@link Subscriber} optimized for the value type and configuration.
     * Implementation selection logic matches {@link #createPublisherFromValue}.
     *
     * @param key    NetworkTables entry key
     * @param value  Initial value (determines type handling)
     * @param config Configuration specifying struct preferences and mappings
     * @param <T>    Data type of the value
     * @return Subscriber implementation best suited for the value type
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows({IllegalAccessException.class, NoSuchFieldException.class})
    public static <T> Subscriber<T> createSubscriberFromValue(String key, T value, Configuration config) {
        Class<T> valueTypeClass = (Class<T>) value.getClass();

        if (StructSerializable.class.isAssignableFrom(valueTypeClass)) {
            Struct<T> struct = (Struct<T>) valueTypeClass.getField("struct").get(null);
            StructOptions option = config.getStructOptions() == null ? Dashboard.config.getStructOptions() : config.getStructOptions();

            return switch (option) {
                case STRUCT -> new StructValueEntry<>(key, struct, value);
                case SUB_TABLE -> new SubtableEntry<>(key, struct, value);
                case MAPPING -> new ValueEntry<>(key, valueTypeClass, value, config);
            };
        }

        return new ValueEntry<>(key, valueTypeClass, value, config);
    }
}
