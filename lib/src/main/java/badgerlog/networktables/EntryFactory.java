package badgerlog.networktables;

import badgerlog.Dashboard;
import badgerlog.annotations.StructOptions;
import badgerlog.annotations.configuration.Configuration;
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
public final class EntryFactory {
    private EntryFactory() {
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows({IllegalAccessException.class, NoSuchFieldException.class})
    public static <T> NTEntry<T> createNetworkTableEntryFromValue(String key, T value, Configuration config) {
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
