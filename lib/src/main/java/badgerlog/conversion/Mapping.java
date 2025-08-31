package badgerlog.conversion;

import badgerlog.annotations.configuration.Configuration;
import edu.wpi.first.networktables.NetworkTableType;
import lombok.Getter;

/**
 * Internal abstract class used by BadgerLog to represent a mapping between two types.
 *
 * @param <StartType> the starting type of the mapping
 * @param <NTType> the ending type of the mapping
 */
@SuppressWarnings("InvalidBlockTag")
@Getter
public abstract class Mapping<StartType, NTType> {

    /**
     * {@return a class representing the starting type}
     */
    private final Class<StartType> startType;

    /**
     * {@return a class representing the ending type}
     */
    private final Class<NTType> tableType;

    /**
     * {@return the NetworkTable type specified by this mapping}
     * It should be the same type as the ending type
     */
    private final NetworkTableType networkTableType;

    /**
     * Creates a new Mapping with the specified parameters.
     *
     * @param startType the class representing the starting type of the Mapping
     * @param tableType the class representing the ending type of the Mapping
     * @param ntType the {@link NetworkTableType} associated with the {@code tableType}
     */
    public Mapping(Class<StartType> startType, Class<NTType> tableType, NetworkTableType ntType) {
        this.startType = startType;
        this.tableType = tableType;
        this.networkTableType = ntType;
    }

    /**
     * {@return a boolean indicating whether the class matches the mapping's startType}
     *
     * @param fieldType the starting type to match
     */
    public boolean matches(Class<?> fieldType) {
        return fieldType != null && (this.startType.isAssignableFrom(fieldType) || fieldType
                .isAssignableFrom(this.startType));
    }

    /**
     * Converts a starting value to the ending value using the configuration
     *
     * @param startValue the start value to convert
     * @param config the configuration to use when converting
     *
     * @return the converted value
     */
    public abstract NTType toNT(StartType startValue, Configuration config);

    /**
     * Converts an ending value to the starting value using the specified configuration
     *
     * @param ntValue the end value to convert
     * @param config the configuration to use when converting
     *
     * @return the converted value
     */
    public abstract StartType toStart(NTType ntValue, Configuration config);
}
