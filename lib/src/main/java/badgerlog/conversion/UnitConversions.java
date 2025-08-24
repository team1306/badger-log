package badgerlog.conversion;

import badgerlog.processing.Fields;
import edu.wpi.first.units.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Radians;

public final class UnitConversions {

    public static final Map<String, Unit> units = new HashMap<>();

    static {
        Field[] fields = Units.class.getFields();

        HashMap<Unit, Set<String>> parts = Arrays.stream(fields)
                .collect(Collectors.toSet())
                .stream()
                .map(Fields::getFieldValue)
                .map(Unit.class::cast)
                .collect(HashMap::new, (map, unit) -> map.put(unit, new HashSet<>()), HashMap::putAll);

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

    public static UnitConverter<?> createConverter(String toUnit) {
        toUnit = toUnit.toLowerCase(Locale.ROOT);

        if (units.get(toUnit) == null)
            throw new IllegalArgumentException(String.format("Unit type: (%s) may not exist", toUnit));
        return createConverter(units.get(toUnit));
    }

    public static UnitConverter<DistanceUnit> initializeDistanceConverter(UnitConverter<DistanceUnit> converter) {
        return initializeUnitConverter(converter, Meters);
    }

    public static UnitConverter<AngleUnit> initializeRotationConverter(UnitConverter<AngleUnit> converter) {
        return initializeUnitConverter(converter, Radians);
    }

    public static <T extends Unit> UnitConverter<T> initializeUnitConverter(UnitConverter<T> converter, T defaultUnit) {
        return converter == null ? createConverter(defaultUnit) : converter;
    }
} 
