package badgerlog.networktables.mappings;

import edu.wpi.first.networktables.NetworkTableType;

import java.util.HashSet;
import java.util.Set;

/**
 * Class containing a list of all {@link Mapping Mappings} for every registered type and providing methods to find specific mappings.
 */
public final class Mappings {
    /**
     * A Set containing all the registered mappings
     *
     * @see MappingType
     */
    public static final Set<Mapping<?, ?>> mappings = new HashSet<>();

    private Mappings() {
    }

    /**
     * Find a {@link Mapping} from a type class. It is assumed that only one mapping per class type is created, and will throw an error if there are more than one present
     *
     * @param type        the {@link Class} representing
     * @param <FieldType> the type on the field
     * @param <NTType>    the {@link Object} representation of a {@link NetworkTableType}
     * @return the Mapping with the specified input type, and a valid NetworkTableType
     */
    @SuppressWarnings("unchecked")
    public static <FieldType, NTType> Mapping<FieldType, NTType> findMapping(Class<FieldType> type) {
        var filteredMappings = mappings.stream().filter(mapping -> mapping.matches(type)).toList();
        if (filteredMappings.isEmpty()) throw new IllegalArgumentException("No mapping found for " + type);
        if (filteredMappings.size() > 1) throw new IllegalArgumentException("Multiple mapping found for " + type);

        return (Mapping<FieldType, NTType>) filteredMappings.get(0);
    }

    /**
     * Finds the {@link NetworkTableType} given a class type
     *
     * @param type the type as a {@link Class}
     * @return the NetworkTableType
     */
    public static NetworkTableType findMappingType(Class<?> type) {
        return findMapping(type).getNetworkTableType();
    }
}
