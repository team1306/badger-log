package badgerlog.conversion;

import badgerlog.conversion.internal.BaseMappings;
import badgerlog.conversion.internal.TransformMappings;
import badgerlog.conversion.internal.UnitMappings;
import edu.wpi.first.networktables.NetworkTableType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Finds and registers mappings for use in the Mapping system. 
 * 
 * <p>Maintains a list of mappings, so that there is only one mapping per starting type; there are never two mappings for the same class.</p>
 */
public final class Mappings {

    private static final Map<Class<?>, Mapping<?, ?>> mappings = new HashMap<>();

    static {
        UnitMappings.registerAllMappings();
        TransformMappings.registerAllMappings();
        BaseMappings.registerAllMappings();
    }

    private Mappings() {
    }

    /**
     * Finds a {@link Mapping} with the specified type class. 
     * 
     * <p>It checks to ensure there is exactly one Mapping for the class.</p>
     *
     * @param type the class representing the type
     * @param <T> the initial type of the Mapping
     *
     * @return the Mapping associated with the type
     */
    @SuppressWarnings("unchecked") // Mapping must have the correct type
    public static <T> Mapping<T, Object> findMapping(Class<T> type) {
        List<Mapping<?, ?>> filteredMappings = mappings.values()
                .stream()
                .filter(mapping -> mapping.matches(type))
                .toList();
        if (filteredMappings.isEmpty()) {
            throw new IllegalArgumentException("No mapping found for " + type);
        }

        return (Mapping<T, Object>) filteredMappings.get(0);
    }

    /**
     * Finds the {@link NetworkTableType} from the Mapping associated with the class.
     *
     * @param type the class representing the type
     *
     * @return the NetworkTable type associated with the Mapping
     */
    public static NetworkTableType findMappingType(Class<?> type) {
        return findMapping(type).getNetworkTableType();
    }

    /**
     * Registers a mapping to the set of Mappings.
     *
     * @param mapping the mapping to register
     */
    public static void registerMapping(Mapping<?, ?> mapping) {
        if (mappings.get(mapping.getClass()) != null) {
            throw new IllegalStateException("Mapping already registered for: " + mapping.getStartType());
        }
        mappings.put(mapping.getStartType(), mapping);
    }

    /**
     * Registers a list of mappings.
     *
     * @param mappings the mappings to register
     */
    public static void registerAllMappings(Mapping<?, ?>... mappings) {
        for (Mapping<?, ?> mapping : mappings) {
            registerMapping(mapping);
        }
    }
}
