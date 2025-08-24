package badgerlog.conversion;

import badgerlog.conversion.internal.BaseMappings;
import badgerlog.conversion.internal.TransformMappings;
import badgerlog.conversion.internal.UnitMappings;
import edu.wpi.first.networktables.NetworkTableType;

import java.util.HashSet;
import java.util.Set;

public final class Mappings {

    private static final Set<Mapping<?, ?>> mappings = new HashSet<>();

    static {
        UnitMappings.registerAllMappings();
        TransformMappings.registerAllMappings();
        BaseMappings.registerAllMappings();
    }

    private Mappings() {
    }

    @SuppressWarnings("unchecked") // Mapping must have the correct type 
    public static <StartType> Mapping<StartType, Object> findMapping(Class<StartType> type) {
        var filteredMappings = mappings.stream().filter(mapping -> mapping.matches(type)).toList();
        if (filteredMappings.isEmpty()) throw new IllegalArgumentException("No mapping found for " + type);
        if (filteredMappings.size() > 1) throw new IllegalArgumentException("Multiple mapping found for " + type);

        return (Mapping<StartType, Object>) filteredMappings.get(0);
    }

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
