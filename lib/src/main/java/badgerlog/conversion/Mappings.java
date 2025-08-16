package badgerlog.conversion;

import badgerlog.conversion.internal.BaseMappings;
import badgerlog.conversion.internal.TransformMappings;
import badgerlog.conversion.internal.UnitMappings;
import edu.wpi.first.networktables.NetworkTableType;

import java.util.HashSet;
import java.util.Set;

/**
 * A registry of all registered {@link Mapping} instances and utilities to locate them by type.
 * <p>
 * This class maintains a static collection of mappings that are automatically populated from
 * fields annotated with. Provides lookup methods to retrieve
 * specific mappings and their associated NetworkTable types.
 */
public final class Mappings {

    /**
     * A static collection of all registered mappings. Fields annotated with
     * <p>
     * are automatically added to this set. This set is used to look up mappings by type.
     */
    private static final Set<Mapping<?, ?>> mappings = new HashSet<>();

    static {
        UnitMappings.registerAllMappings();
        TransformMappings.registerAllMappings();
        BaseMappings.registerAllMappings();
    }

    private Mappings() {
    }

    /**
     * Retrieves the unique {@link Mapping} associated with the specified starting type.
     *
     * @param type        the class of the start type to search for
     * @param <StartType> the starting type
     * @return the unique mapping for the given start type
     * @throws IllegalArgumentException if no mapping or multiple mappings exist for {@code type}
     */
    @SuppressWarnings("unchecked") // Mapping must have the correct type 
    public static <StartType> Mapping<StartType, Object> findMapping(Class<StartType> type) {
        var filteredMappings = mappings.stream().filter(mapping -> mapping.matches(type)).toList();
        if (filteredMappings.isEmpty()) throw new IllegalArgumentException("No mapping found for " + type);
        if (filteredMappings.size() > 1) throw new IllegalArgumentException("Multiple mapping found for " + type);

        return (Mapping<StartType, Object>) filteredMappings.get(0);
    }

    /**
     * Finds the {@link NetworkTableType} associated with the specified class type.
     *
     * @param type the class type to search for
     * @return the corresponding NetworkTable type
     * @throws IllegalArgumentException if no mapping or multiple mappings exist for {@code type}
     * @see #findMapping(Class)
     */
    public static NetworkTableType findMappingType(Class<?> type) {
        return findMapping(type).getNetworkTableType();
    }

    public static void registerMapping(Mapping<?, ?> mapping) {
        mappings.add(mapping);
    }

    public static void registerAllMappings(Mapping<?, ?>... mappings) {
        for (Mapping<?, ?> mapping : mappings) {
            registerMapping(mapping);
        }
    }
}
