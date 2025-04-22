package badgerlog.networktables.mappings.conversion;

import badgerlog.networktables.DashboardUtil;
import edu.wpi.first.units.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Radians;

public class UnitConversions {

    public static final Map<String, Unit> units;

    static {
        var fields = Units.class.getFields();

        units = Arrays.stream(fields)
                .map(DashboardUtil::getFieldValue)
                .map(Unit.class::cast)
                .collect(HashMap::new, (map, unit) -> map.put(unit.name(), unit), HashMap::putAll);
    }

    public static double convert(double value, @Nonnull String fromUnit, @Nonnull String toUnit) {
        if (units.get(fromUnit) == null)
            throw new IllegalArgumentException(String.format("Unit type: (%s) may not exist", fromUnit));
        if(units.get(toUnit) == null)
            throw new IllegalArgumentException(String.format("Unit type: (%s) may not exist", toUnit));
        return convert(value, units.get(fromUnit), units.get(toUnit));
    }

    public static <T extends Unit, N extends Unit> double convert(double value, @Nonnull T fromUnit, @Nonnull N toUnit) {
        if (!fromUnit.getBaseUnit().equals(toUnit.getBaseUnit()))
            throw new IllegalArgumentException("Unit types do not match");
        return toUnit.getConverterFromBase().apply(fromUnit.getConverterToBase().apply(value));
    }

    public static <T extends Unit> UnitConverter<T> createConverter(@Nonnull T tounit) {
        return new UnitConverter<>() {
            @Override
            public double convertTo(@NotNull Measure<T> value) {
                return value.in(tounit);
            }

            @Override
            @SuppressWarnings("unchecked") // the type is guaranteed to be of type Measure<T> because of Unit implementation
            public @Nonnull Measure<T> convertFrom(double value) {
                return (Measure<T>) tounit.of(value);
            }
        };
    }

    public static @Nonnull UnitConverter<?> createConverter(@Nonnull String toUnit) {
        if (units.get(toUnit) == null)
            throw new IllegalArgumentException(String.format("Unit type: (%s) may not exist", toUnit));
        return createConverter(units.get(toUnit));
    }

    public static @Nonnull UnitConverter<DistanceUnit> initializeDistanceConverter(@Nullable UnitConverter<DistanceUnit> converter) {
        return initializeUnitConverter(converter, Meters);
    }

    public static @Nonnull UnitConverter<AngleUnit> initializeRotationConverter(@Nullable UnitConverter<AngleUnit> converter) {
        return initializeUnitConverter(converter, Radians);
    }

    public static <T extends Unit> @Nonnull UnitConverter<T> initializeUnitConverter(@Nullable UnitConverter<T> converter, @Nonnull T defaultUnit) {
        if (converter == null) return createConverter(defaultUnit);
        Measure<T> converterValue = converter.convertFrom(0);
        if (!converterValue.baseUnit().equivalent(defaultUnit.getBaseUnit()))
            throw new IllegalArgumentException(String.format("Base unit types: (%s) and (%s) do not match", converterValue.baseUnit().name(), defaultUnit.getBaseUnit().name()));
        else return converter;
    }
} 
