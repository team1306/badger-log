package badgerlog.networktables.mappings;

import edu.wpi.first.networktables.NetworkTableType;
import lombok.Getter;

/**
 * Class to map an arbitrary type to a {@link NetworkTableType}
 *
 * @param <StartType> the starting type
 * @param <NTType>    the {@link Object} form of a NetworkTableType
 */
@SuppressWarnings("JavadocDeclaration")
@Getter
public abstract class Mapping<StartType, NTType> {
    /**
     * The starting type as a {@link Class}
     * @return the starting type
     */
    private final Class<StartType> fieldType;
    /**
     * The {@link NetworkTableType} as a {@link Class}
     * @return the type on NetworkTables
     */
    private final Class<NTType> tableType;

    /**
     * The {@link NetworkTableType} for NetworkTables
     * @return the NetworkTableType
     */
    private final NetworkTableType networkTableType;

    /**
     * Construct a new mapping for a starting type and NetworkTable type
     *
     * @param fieldType the starting type
     * @param tableType the type on NetworkTables
     * @param ntType    the {@link NetworkTableType}
     */
    public Mapping(Class<StartType> fieldType, Class<NTType> tableType, NetworkTableType ntType) {
        this.fieldType = fieldType;
        this.tableType = tableType;
        this.networkTableType = ntType;
    }

    /**
     * Check if the type of field matches another. It is assumed that there is only have one mapping per starting type
     *
     * @param fieldType the starting type
     * @return if this {@link Mapping} matches the type given
     */
    public boolean matches(Class<?> fieldType) {
        return this.fieldType.isAssignableFrom(fieldType) || fieldType.isAssignableFrom(this.fieldType);
    }

    /**
     * A function to map the starting type to a NetworkTable type given a config and value
     *
     * @param fieldValue the value to map
     * @param config     the configuration in the form of a {@link String}
     * @return the value in the NetworkTable type
     * @see UnitMappings
     */
    public abstract NTType toNT(StartType fieldValue, String config);

    /**
     * A function to map a NetworkTable type to the starting type
     *
     * @param ntValue the value given by NetworkTables
     * @param config  the configuration in the form of a string
     * @return the value in the starting type
     * @see UnitMappings
     */
    public abstract StartType toField(NTType ntValue, String config);
}
