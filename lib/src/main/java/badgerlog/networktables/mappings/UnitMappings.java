package badgerlog.networktables.mappings;

import badgerlog.entry.configuration.Configuration;
import badgerlog.networktables.mappings.conversion.UnitConversions;
import badgerlog.networktables.mappings.conversion.UnitConverter;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.units.*;
import edu.wpi.first.units.measure.*;

import static edu.wpi.first.units.Units.*;

/**
 * Collection of {@link Mapping Mappings} for NetworkTables, includes all implementations of {@link Measure}, rotation, translation, twist, and pose mappings
 */
public final class UnitMappings {

    
    /**
     * Mapping of {@link Distance} to {@link Double}
     */
    @MappingType
    public static Mapping<Distance, Double> distanceMapping = createMeasureMapping(Meters, Distance.class);

    /**
     * Mapping of {@link Angle} to {@link Double}
     */
    @MappingType
    public static Mapping<Angle, Double> angleMapping = createMeasureMapping(Radians, Angle.class);

    /**
     * Mapping of {@link Time} to {@link Double}
     */
    @MappingType
    public static Mapping<Time, Double> timeMapping = createMeasureMapping(Seconds, Time.class);

    /**
     * Mapping of {@link LinearVelocity} to {@link Double}
     */
    @MappingType
    public static Mapping<LinearVelocity, Double> linearVelocityMapping = createMeasureMapping(MetersPerSecond, LinearVelocity.class);

    /**
     * Mapping of {@link AngularVelocity} to {@link Double}
     */
    @MappingType
    public static Mapping<AngularVelocity, Double> angularVelocityMapping = createMeasureMapping(RadiansPerSecond, AngularVelocity.class);

    /**
     * Mapping of {@link Frequency} to {@link Double}
     */
    @MappingType
    public static Mapping<Frequency, Double> frequencyMapping = createMeasureMapping(Hertz, Frequency.class);

    /**
     * Mapping of {@link LinearAcceleration} to {@link Double}
     */
    @MappingType
    public static Mapping<LinearAcceleration, Double> linearAccelerationMapping = createMeasureMapping(MetersPerSecondPerSecond, LinearAcceleration.class);

    /**
     * Mapping of {@link AngularAcceleration} to {@link Double}
     */
    @MappingType
    public static Mapping<AngularAcceleration, Double> angularAccelerationMapping = createMeasureMapping(RadiansPerSecondPerSecond, AngularAcceleration.class);

    /**
     * Mapping of {@link Mass} to {@link Double}
     */
    @MappingType
    public static Mapping<Mass, Double> massMapping = createMeasureMapping(Kilograms, Mass.class);

    /**
     * Mapping of {@link Force} to {@link Double}
     */
    @MappingType
    public static Mapping<Force, Double> forceMapping = createMeasureMapping(Newtons, Force.class);

    /**
     * Mapping of {@link Torque} to {@link Double}
     */
    @MappingType
    public static Mapping<Torque, Double> torqueMapping = createMeasureMapping(NewtonMeter, Torque.class);

    /**
     * Mapping of {@link LinearMomentum} to {@link Double}
     */
    @MappingType
    public static Mapping<LinearMomentum, Double> linearMomentumMapping = createMeasureMapping(KilogramMetersPerSecond, LinearMomentum.class);

    /**
     * Mapping of {@link AngularMomentum} to {@link Double}
     */
    @MappingType
    public static Mapping<AngularMomentum, Double> angularMomentumMapping = createMeasureMapping(KilogramMetersSquaredPerSecond, AngularMomentum.class);

    /**
     * Mapping of {@link MomentOfInertia} to {@link Double}
     */
    @MappingType
    public static Mapping<MomentOfInertia, Double> momentOfInertiaMapping = createMeasureMapping(KilogramSquareMeters, MomentOfInertia.class);

    /**
     * Mapping of {@link Voltage} to {@link Double}
     */
    @MappingType
    public static Mapping<Voltage, Double> voltageMapping = createMeasureMapping(Volts, Voltage.class);

    /**
     * Mapping of {@link Current} to {@link Double}
     */
    @MappingType
    public static Mapping<Current, Double> currentMapping = createMeasureMapping(Amps, Current.class);

    /**
     * Mapping of {@link Resistance} to {@link Double}
     */
    @MappingType
    public static Mapping<Resistance, Double> resistanceMapping = createMeasureMapping(Ohms, Resistance.class);

    /**
     * Mapping of {@link Energy} to {@link Double}
     */
    @MappingType
    public static Mapping<Energy, Double> energyMapping = createMeasureMapping(Joules, Energy.class);

    /**
     * Mapping of {@link Power} to {@link Double}
     */
    @MappingType
    public static Mapping<Power, Double> powerMapping = createMeasureMapping(Watts, Power.class);

    /**
     * Mapping of {@link Temperature} to {@link Double}
     */
    @MappingType
    public static Mapping<Temperature, Double> temperatureMapping = createMeasureMapping(Celsius, Temperature.class);
    
    private static <T extends Unit, N extends Measure<T>> Mapping<N, Double> createMeasureMapping(T defaultUnit, Class<N> measureType) {
        return new Mapping<>(measureType, double.class, NetworkTableType.kDouble) {
            @Override
            public Double toNT(N startValue, Configuration config) {
                UnitConverter<T> converter = UnitConversions.initializeUnitConverter(config.getDefaultConverter(), defaultUnit);
                return converter.convertTo(startValue);
            }

            @Override
            public N toStart(Double ntValue, Configuration config) {
                UnitConverter<T> converter = UnitConversions.initializeUnitConverter(config.getDefaultConverter(), defaultUnit);
                return (N) converter.convertFrom(ntValue);
            }
        };
    }
}
