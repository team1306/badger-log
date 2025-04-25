package badgerlog.networktables.mappings;

import badgerlog.entry.configuration.Configuration;
import badgerlog.networktables.mappings.conversion.UnitConversions;
import badgerlog.networktables.mappings.conversion.UnitConverter;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.*;
import org.jetbrains.annotations.NotNull;

import static edu.wpi.first.units.Units.*;

/**
 * Collection of predefined {@link Mapping} implementations for converting unit-based {@link Measure}
 * types to/from NetworkTables {@link Double} values.
 */
public final class UnitMappings {


    /**
     * Maps {@link Distance} measures to NetworkTables double values.
     * Uses {@link Units#Meters} as the base unit.
     */
    @MappingType
    public static Mapping<Distance, Double> distanceMapping = createMeasureMapping(Meters, Distance.class);

    /**
     * Maps {@link Angle} measures to NetworkTables double values.
     * Uses {@link Units#Radians} as the base unit.
     */
    @MappingType
    public static Mapping<Angle, Double> angleMapping = createMeasureMapping(Radians, Angle.class);

    /**
     * Maps {@link Time} measures to NetworkTables double values.
     * Uses {@link Units#Seconds} as the base unit.
     */
    @MappingType
    public static Mapping<Time, Double> timeMapping = createMeasureMapping(Seconds, Time.class);

    /**
     * Maps {@link LinearVelocity} measures to NetworkTables double values.
     * Uses {@link Units#MetersPerSecond} as the base unit.
     */
    @MappingType
    public static Mapping<LinearVelocity, Double> linearVelocityMapping = createMeasureMapping(MetersPerSecond, LinearVelocity.class);

    /**
     * Maps {@link AngularVelocity} measures to NetworkTables double values.
     * Uses {@link Units#RadiansPerSecond} as the base unit.
     */
    @MappingType
    public static Mapping<AngularVelocity, Double> angularVelocityMapping = createMeasureMapping(RadiansPerSecond, AngularVelocity.class);

    /**
     * Maps {@link Frequency} measures to NetworkTables double values.
     * Uses {@link Units#Hertz} as the base unit.
     */
    @MappingType
    public static Mapping<Frequency, Double> frequencyMapping = createMeasureMapping(Hertz, Frequency.class);

    /**
     * Maps {@link LinearAcceleration} measures to NetworkTables double values.
     * Uses {@link Units#MetersPerSecondPerSecond} as the base unit.
     */
    @MappingType
    public static Mapping<LinearAcceleration, Double> linearAccelerationMapping = createMeasureMapping(MetersPerSecondPerSecond, LinearAcceleration.class);

    /**
     * Maps {@link AngularAcceleration} measures to NetworkTables double values.
     * Uses {@link Units#RadiansPerSecondPerSecond} as the base unit.
     */
    @MappingType
    public static Mapping<AngularAcceleration, Double> angularAccelerationMapping = createMeasureMapping(RadiansPerSecondPerSecond, AngularAcceleration.class);

    /**
     * Maps {@link Mass} measures to NetworkTables double values.
     * Uses {@link Units#Kilograms} as the base unit.
     */
    @MappingType
    public static Mapping<Mass, Double> massMapping = createMeasureMapping(Kilograms, Mass.class);

    /**
     * Maps {@link Force} measures to NetworkTables double values.
     * Uses {@link Units#Newtons} as the base unit.
     */
    @MappingType
    public static Mapping<Force, Double> forceMapping = createMeasureMapping(Newtons, Force.class);

    /**
     * Maps {@link Torque} measures to NetworkTables double values.
     * Uses {@link Units#NewtonMeters} as the base unit.
     */
    @MappingType
    public static Mapping<Torque, Double> torqueMapping = createMeasureMapping(NewtonMeter, Torque.class);

    /**
     * Maps {@link LinearMomentum} measures to NetworkTables double values.
     * Uses {@link Units#KilogramMetersPerSecond} as the base unit.
     */
    @MappingType
    public static Mapping<LinearMomentum, Double> linearMomentumMapping = createMeasureMapping(KilogramMetersPerSecond, LinearMomentum.class);

    /**
     * Maps {@link AngularMomentum} measures to NetworkTables double values.
     * Uses {@link Units#KilogramMetersSquaredPerSecond} as the base unit.
     */
    @MappingType
    public static Mapping<AngularMomentum, Double> angularMomentumMapping = createMeasureMapping(KilogramMetersSquaredPerSecond, AngularMomentum.class);

    /**
     * Maps {@link MomentOfInertia} measures to NetworkTables double values.
     * Uses {@link Units#KilogramSquareMeters} as the base unit.
     */
    @MappingType
    public static Mapping<MomentOfInertia, Double> momentOfInertiaMapping = createMeasureMapping(KilogramSquareMeters, MomentOfInertia.class);

    /**
     * Maps {@link Voltage} measures to NetworkTables double values.
     * Uses {@link Units#Volts} as the base unit.
     */
    @MappingType
    public static Mapping<Voltage, Double> voltageMapping = createMeasureMapping(Volts, Voltage.class);

    /**
     * Maps {@link Current} measures to NetworkTables double values.
     * Uses {@link Units#Amps} as the base unit.
     */
    @MappingType
    public static Mapping<Current, Double> currentMapping = createMeasureMapping(Amps, Current.class);

    /**
     * Maps {@link Resistance} measures to NetworkTables double values.
     * Uses {@link Units#Ohms} as the base unit.
     */
    @MappingType
    public static Mapping<Resistance, Double> resistanceMapping = createMeasureMapping(Ohms, Resistance.class);

    /**
     * Maps {@link Energy} measures to NetworkTables double values.
     * Uses {@link Units#Joules} as the base unit.
     */
    @MappingType
    public static Mapping<Energy, Double> energyMapping = createMeasureMapping(Joules, Energy.class);

    /**
     * Maps {@link Power} measures to NetworkTables double values.
     * Uses {@link Units#Watts} as the base unit.
     */
    @MappingType
    public static Mapping<Power, Double> powerMapping = createMeasureMapping(Watts, Power.class);

    /**
     * Maps {@link Temperature} measures to NetworkTables double values.
     * Uses {@link Units#Celsius} as the base unit.
     */
    @MappingType
    public static Mapping<Temperature, Double> temperatureMapping = createMeasureMapping(Celsius, Temperature.class);

    private static <T extends Unit, N extends Measure<T>> Mapping<N, Double> createMeasureMapping(T defaultUnit, Class<N> measureType) {
        return new Mapping<>(measureType, double.class, NetworkTableType.kDouble) {
            @Override
            public Double toNT(@NotNull N startValue, @NotNull Configuration config) {
                UnitConverter<T> converter = UnitConversions.initializeUnitConverter(config.getDefaultConverter(), defaultUnit);
                return converter.convertTo(startValue);
            }

            @Override
            @SuppressWarnings("unchecked") // N has to be able to cast to Measure<T> to fulfill the generic requirement
            public N toStart(@NotNull Double ntValue, @NotNull Configuration config) {
                UnitConverter<T> converter = UnitConversions.initializeUnitConverter(config.getDefaultConverter(), defaultUnit);
                return (N) converter.convertFrom(ntValue);
            }
        };
    }
}
