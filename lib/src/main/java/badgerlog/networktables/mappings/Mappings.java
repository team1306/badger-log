package badgerlog.networktables.mappings;

import edu.wpi.first.networktables.NetworkTableType;

import java.util.ArrayList;
import java.util.List;

/**
 * Class containing all mappings for each type. On startup, searches for any fields annotated with {@link MappingType} and adds them to the list of current mappings.
 */
public class Mappings {
    /**
     * A List containing all the registered mappings 
     * @see MappingType
     */
    public static final List<Mapping<?, ?>> mappings = new ArrayList<>();

    /**
     * Find a mapping based off the field type. It is assumed that only one mapping per field type is created, and will throw an error if there are more than one present
     * @param type the type of mapping to search for
     * @return the mapping with the specified input, and a valid NetworkTableOutput
     * @param <FieldType> the type specified by the field
     * @param <NTType> the NetworkTableType
     */
    @SuppressWarnings("unchecked")
    public static <FieldType, NTType> Mapping<FieldType, NTType> findMapping(Class<FieldType> type) {
        var filteredMappings = mappings.stream().filter(mapping -> mapping.matches(type)).toList();
        if (filteredMappings.isEmpty()) throw new IllegalArgumentException("No mapping found for " + type);
        if (filteredMappings.size() > 1) throw new IllegalArgumentException("Multiple mapping found for " + type);

        return (Mapping<FieldType, NTType>) filteredMappings.get(0);
    }

    /**
     * Finds the NetworkTable type given a field type class
     * @param type the field type
     * @return the NetworkTableType
     */
    public static NetworkTableType findMappingType(Class<?> type) {
        return findMapping(type).getNetworkTableType();
    }
}
