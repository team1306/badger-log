package badgerlog.networktables.mappings.conversion;

import badgerlog.networktables.DashboardUtil;
import edu.wpi.first.units.*;

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
                .map(DashboardUtil::<Unit>getFieldValue)
                .collect(HashMap::new, (map, unit) -> map.put(unit.name(), unit), HashMap::putAll);
    }

    public static double convert(double value, String fromUnit, String toUnit) {
        if(units.get(fromUnit) == null || units.get(toUnit) == null) throw new IllegalArgumentException(String.format("Unit types: (%s) and (%s) may not exist", fromUnit, toUnit));
        return convert(value, units.get(fromUnit), units.get(toUnit));
    }

    public static <T extends Unit, N extends Unit> double convert(double value, T fromUnit, N toUnit) {
        if (!fromUnit.getBaseUnit().equals(toUnit.getBaseUnit()))
            throw new IllegalArgumentException("Unit types do not match");
        return toUnit.getConverterFromBase().apply(fromUnit.getConverterToBase().apply(value));
    }

    public static <T extends Unit> UnitConverter<T> createConverter(T tounit) {
        return new UnitConverter<>() {
            @Override
            public double convertTo(Measure<T> value) {
                return value.in(tounit);
            }

            @Override
            public Measure<T> convertFrom(double value) {
                return (Measure<T>) tounit.of(value);
            }
        };
    }
    
    public static <T extends Unit> UnitConverter<T> createConverter(String toUnit) {
        if(units.get(toUnit) == null) throw new IllegalArgumentException(String.format("Unit type: (%s) may not exist", toUnit));
        return createConverter((T) units.get(toUnit));
    }
    
    public static UnitConverter<DistanceUnit> initializeDistanceConverter(UnitConverter<DistanceUnit> converter){
        return initializeUnitConverter(converter, Meters);
    }

    public static UnitConverter<AngleUnit> initializeRotationConverter(UnitConverter<AngleUnit> converter){
        return initializeUnitConverter(converter, Radians);
    }
    
    public static <T extends Unit> UnitConverter<T> initializeUnitConverter(UnitConverter<T> converter, T defaultUnit){
        if(converter == null) return createConverter(defaultUnit);
        Measure<T> converterValue = converter.convertFrom(0);
        if(!converterValue.baseUnit().equivalent(defaultUnit.getBaseUnit())) throw new IllegalArgumentException(String.format("Base unit types: (%s) and (%s) do not match", converterValue.baseUnit().name(), defaultUnit.getBaseUnit().name()));
        else return converter;
    }
} 
