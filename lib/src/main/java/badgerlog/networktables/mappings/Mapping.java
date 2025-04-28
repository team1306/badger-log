package badgerlog.networktables.mappings;

import badgerlog.entry.Configuration;
import edu.wpi.first.networktables.NetworkTableType;
import lombok.Getter;

import javax.annotation.Nonnull;

/**
 * Abstract base class defining bidirectional conversions between an arbitrary type and a NetworkTables-compatible type.
 * <p>
 * Subclasses must implement {@link #toNT} and {@link #toStart} to provide type conversion logic. Each mapping
 * associates a type ({@code StartType}) with a {@link NetworkTableType} and its object representation ({@code NTType}).
 * Used in conjunction with {@link Mappings} for centralized registration and lookup.
 *
 * @param <StartType> Source type for conversions
 * @param <NTType>    NetworkTables-compatible type
 */
@SuppressWarnings("JavadocDeclaration")
@Getter
public abstract class Mapping<StartType, NTType> {
    /**
     * The starting class type being mapped from/to.
     *
     * @return the class object representing the source type
     */
    private final Class<StartType> fieldType;

    /**
     * The NetworkTables-compatible class type.
     *
     * @return the class object representing the NetworkTable-compatible type
     */
    private final Class<NTType> tableType;

    /**
     * {@link NetworkTableType} enum value corresponding to {@code NTType}.
     *
     * @return the NetworkTableType enum constant
     */
    private final NetworkTableType networkTableType;

    /**
     * Constructs a mapping between a starting type and a NetworkTables type.
     *
     * @param startType Starting type to map
     * @param tableType NetworkTables-compatible type class
     * @param ntType    {@link NetworkTableType} enum value
     */
    public Mapping(@Nonnull Class<StartType> startType, @Nonnull Class<NTType> tableType, @Nonnull NetworkTableType ntType) {
        this.fieldType = startType;
        this.tableType = tableType;
        this.networkTableType = ntType;
    }

    /**
     * Checks whether this mapping supports a given type via assignability.
     *
     * @param fieldType Type to check compatibility with {@link #fieldType}
     * @return {@code true} if {@code fieldType} is assignable to/from this mapping's {@code StartType}
     */
    public boolean matches(Class<?> fieldType) {
        return fieldType != null && (this.fieldType.isAssignableFrom(fieldType) || fieldType.isAssignableFrom(this.fieldType));
    }

    /**
     * Converts a {@code StartType} value to its NetworkTables-compatible form.
     *
     * @param startValue Value to convert
     * @param config     Configuration context for the conversion
     * @return Converted value as a {@code NTType}
     */
    public abstract NTType toNT(@Nonnull StartType startValue, @Nonnull Configuration config);

    /**
     * Converts a NetworkTables-compatible value back to the original {@code StartType}.
     *
     * @param ntValue NetworkTable value to convert
     * @param config  Configuration context for the conversion
     * @return Converted value as a {@code StartType}
     */
    public abstract StartType toStart(@Nonnull NTType ntValue, @Nonnull Configuration config);
}
