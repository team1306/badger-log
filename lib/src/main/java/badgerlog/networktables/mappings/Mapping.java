package badgerlog.networktables.mappings;

import edu.wpi.first.networktables.NetworkTableType;
import lombok.Getter;

/**
 * Mapping class to map a type on a field to a valid NetworkTable type
 *
 * @param <FieldType> the type on a field
 * @param <NTType>    the valid NetworkTable type
 */
@Getter
public abstract class Mapping<FieldType, NTType> {
    /**
     * The Class type of the field
     *
     * @return the Class representing the type of the field
     */
    private final Class<FieldType> fieldType;
    /**
     * The Class type on NetworkTables
     *
     * @return the Class representing the type on NetworkTables
     */
    private final Class<NTType> tableType;

    /**
     * The {@link NetworkTableType} for NetworkTables
     *
     * @return the {@link NetworkTableType} for NetworkTables
     */
    private final NetworkTableType networkTableType;

    /**
     * Construct a new mapping for a field and NetworkTable value
     *
     * @param fieldType the type of the field
     * @param tableType the type on NetworkTables
     * @param ntType    the {@link NetworkTableType}
     */
    public Mapping(Class<FieldType> fieldType, Class<NTType> tableType, NetworkTableType ntType) {
        this.fieldType = fieldType;
        this.tableType = tableType;
        this.networkTableType = ntType;
    }

    /**
     * Check if the type of field matches, since it is assumed to only have one mapping per field type
     *
     * @param fieldType the type of field
     * @return if this mapping matches the type given
     */
    public boolean matches(Class<?> fieldType) {
        return this.fieldType.isAssignableFrom(fieldType) || fieldType.isAssignableFrom(this.fieldType);
    }

    /**
     * A function to map the field type to a NetworkTable type given a config and value
     *
     * @param fieldValue the value of the field to map
     * @param config     the configuration in the form of a string, see {@link UnitMappings} for an example of how to use
     * @return the value in the NetworkTable type
     */
    public abstract NTType toNT(FieldType fieldValue, String config);

    /**
     * A function to map a NetworkTable type to the field type
     *
     * @param ntValue the value given by NetworkTables
     * @param config  the configuration in the form of a string, see {@link UnitMappings} for an example of how to use
     * @return the value in the field type
     */
    public abstract FieldType toField(NTType ntValue, String config);
}
