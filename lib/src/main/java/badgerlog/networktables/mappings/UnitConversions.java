package badgerlog.networktables.mappings;

import badgerlog.DashboardUtil;
import edu.wpi.first.units.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Radians;

/**
 * Provides static utilities for unit conversions and {@link UnitConverter} management.
 * <p>
 * Supports conversion between compatible units and creation/validation
 * of type-safe unit converters. Maintains a registry of available units loaded via reflection
 * from {@link Units}.
 */
public final class UnitConversions { // todo could possibly replace with wpilib units if structured properly
    /**
     * Static registry of available units, keyed by their {@link Unit#name()}.
     * <p>
     * Populated at class initialization by reflecting over fields in {@link Units}.
     */
    public static final Map<String, Unit> units;

    static {
        var fields = Units.class.getFields();

        units = Arrays.stream(fields)
                .map(DashboardUtil::getFieldValue)
                .map(Unit.class::cast)
                .collect(HashMap::new, (map, unit) -> map.put(unit.name(), unit), HashMap::putAll);
    }

    private UnitConversions() {
    }

    /**
     * Converts a numeric value between two units specified by name.
     *
     * @param value    Numeric value to convert
     * @param fromUnit Source unit name
     * @param toUnit   Target unit name
     * @return Converted value in target unit
     * @throws IllegalArgumentException If either unit name is not registered
     * @throws IllegalArgumentException If units are incompatible (different base types)
     */
    public static double convert(double value, @Nonnull String fromUnit, @Nonnull String toUnit) {
        if (units.get(fromUnit) == null)
            throw new IllegalArgumentException(String.format("Unit type: (%s) may not exist", fromUnit));
        if (units.get(toUnit) == null)
            throw new IllegalArgumentException(String.format("Unit type: (%s) may not exist", toUnit));
        return convert(value, units.get(fromUnit), units.get(toUnit));
    }

    /**
     * Converts a numeric value between two {@link Unit} instances.
     *
     * @param value    Numeric value to convert
     * @param fromUnit Source unit instance
     * @param toUnit   Target unit instance
     * @param <T> the starting unit
     * @param <N> the ending unit
     * @return Converted value in target unit
     * @throws IllegalArgumentException If units are incompatible (different base types)
     */
    public static <T extends Unit, N extends Unit> double convert(double value, @Nonnull T fromUnit, @Nonnull N toUnit) {
        if (!fromUnit.getBaseUnit().equals(toUnit.getBaseUnit()))
            throw new IllegalArgumentException("Unit types do not match");
        return toUnit.getConverterFromBase().apply(fromUnit.getConverterToBase().apply(value));
    }

    /**
     * Creates a {@link UnitConverter} for a specific unit type.
     *
     * @param toUnit Unit to convert to/from
     * @param <T> a converter using the specified unit
     * @return Converter instance for the specified unit
     */
    public static <T extends Unit> UnitConverter<T> createConverter(@Nonnull T toUnit) {
        return new UnitConverter<>() {
            @Override
            public double convertTo(@Nonnull Measure<T> value) {
                return value.in(toUnit);
            }

            @Override
            @SuppressWarnings("unchecked")
            // the type is guaranteed to be of type Measure<T> because of Unit implementation
            public @Nonnull Measure<T> convertFrom(double value) {
                return (Measure<T>) toUnit.of(value);
            }
        };
    }

    /**
     * Creates a {@link UnitConverter} by unit name.
     *
     * @param toUnit Target unit name (e.g., "Radians")
     * @return Converter instance for the specified unit
     * @throws IllegalArgumentException If unit name is not registered
     */
    public static @Nonnull UnitConverter<?> createConverter(@Nonnull String toUnit) {
        if (units.get(toUnit) == null)
            throw new IllegalArgumentException(String.format("Unit type: (%s) may not exist", toUnit));
        return createConverter(units.get(toUnit));
    }

    /**
     * Initializes or validates a distance unit converter with default fallback to meters.
     *
     * @param converter Existing converter (may be null)
     * @return Valid converter matching distance units
     */
    public static @Nonnull UnitConverter<DistanceUnit> initializeDistanceConverter(@Nullable UnitConverter<DistanceUnit> converter) {
        return initializeUnitConverter(converter, Meters);
    }

    /**
     * Initializes or validates an angle unit converter with default fallback to radians.
     *
     * @param converter Existing converter (may be null)
     * @return Valid converter matching angle units
     */
    public static @Nonnull UnitConverter<AngleUnit> initializeRotationConverter(@Nullable UnitConverter<AngleUnit> converter) {
        return initializeUnitConverter(converter, Radians);
    }

    /**
     * Generic initializer for unit converters with null safety checks.
     *
     * @param converter   Existing converter (null uses defaultUnit)
     * @param defaultUnit Fallback unit type if converter is null
     * @param <T> the unit to use
     * @return Validated or newly created converter
     */
    public static <T extends Unit> @Nonnull UnitConverter<T> initializeUnitConverter(@Nullable UnitConverter<T> converter, @Nonnull T defaultUnit) {
        return converter == null ? createConverter(defaultUnit) : converter;
    }
} 
