package badgerlog.conversion;

import badgerlog.processing.Fields;
import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.DistanceUnit;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;
import edu.wpi.first.units.Units;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Radians;

/**
 * Internal class used by BadgerLog to manage and create {@link UnitConverter}.
 * This wraps the WPILib Units system into an interface that can use any string form of the unit.
 */
public final class UnitConversions {

    public static final Map<String, Unit> units = new HashMap<>();

    static {
        Field[] fields = Units.class.getFields();

        HashMap<Unit, Set<String>> parts = Arrays.stream(fields)
                .collect(Collectors.toSet())
                .stream()
                .map(Fields::getFieldValue)
                .map(Unit.class::cast)
                .collect(
                        HashMap::new, (map, unit) -> map.put(unit, new HashSet<>()), HashMap::putAll);

        for (Field field : fields) {
            Unit unit = (Unit) Fields.getFieldValue(field);
            parts.get(unit).add(unit.name().toLowerCase(Locale.ROOT));
            parts.get(unit).add(unit.symbol().toLowerCase(Locale.ROOT));
            parts.get(unit).add(field.getName().toLowerCase(Locale.ROOT));
        }

        for (Unit part : parts.keySet()) {
            Set<String> aliases = parts.get(part);
            aliases.forEach(alias -> units.put(alias, part));
        }
    }

    private UnitConversions() {
    }

    /**
     * Creates an implementation of a {@link UnitConverter} that uses the {@code toUnit} as its base.
     *
     * @param toUnit the unit to convert to and from
     * @param <T> the type of the Unit
     *
     * @return the created UnitConverter
     */
    public static <T extends Unit> UnitConverter<T> createConverter(T toUnit) {
        return new UnitConverter<>() {
            @Override
            public double convertTo(Measure<T> value) {
                return value.in(toUnit);
            }

            @Override
            @SuppressWarnings("unchecked")
            // the type is guaranteed to be of type Measure<T> because of Unit implementation
            public Measure<T> convertFrom(double value) {
                return (Measure<T>) toUnit.of(value);
            }
        };
    }

    /**
     * Creates a {@link UnitConverter} from a unit represented as a String.
     *
     * @param toUnit the unit as a string
     *
     * @return the created UnitConverter
     */
    public static UnitConverter<?> createConverter(String toUnit) {
        toUnit = toUnit.toLowerCase(Locale.ROOT);

        if (units.get(toUnit) == null) {
            throw new IllegalArgumentException(String.format("Unit type: (%s) may not exist", toUnit));
        }
        return createConverter(units.get(toUnit));
    }

    /**
     * {@code defaultUnit} defaults to {@code Meters}
     *
     * @see #initializeUnitConverter(UnitConverter, Unit)
     */
    public static UnitConverter<DistanceUnit> initializeDistanceConverter(UnitConverter<DistanceUnit> converter) {
        return initializeUnitConverter(converter, Meters);
    }

    /**
     * {@code defaultUnit} defaults to {@code Radians}
     *
     * @see #initializeUnitConverter(UnitConverter, Unit)
     */
    public static UnitConverter<AngleUnit> initializeRotationConverter(UnitConverter<AngleUnit> converter) {
        return initializeUnitConverter(converter, Radians);
    }

    /**
     * Initializes a potentially null converter with the default unit converter if null, otherwise returns the
     * converter.
     *
     * @param converter the converter to check
     * @param defaultUnit the default unit form of the converter
     * @param <T> the unit type
     *
     * @return the non-null initialized or passed converter
     */
    public static <T extends Unit> UnitConverter<T> initializeUnitConverter(UnitConverter<T> converter, T defaultUnit) {
        return converter == null ? createConverter(defaultUnit) : converter;
    }
}
