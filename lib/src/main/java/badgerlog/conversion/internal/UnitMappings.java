package badgerlog.conversion.internal;

import badgerlog.annotations.configuration.Configuration;
import badgerlog.conversion.Mapping;
import badgerlog.conversion.Mappings;
import badgerlog.conversion.UnitConversions;
import badgerlog.conversion.UnitConverter;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;
import edu.wpi.first.units.measure.*;

import static edu.wpi.first.units.Units.*;

public final class UnitMappings {

    private static final Mapping<Distance, Double> distanceMapping = createMeasureMapping(Meters, Distance.class);
    private static final Mapping<Angle, Double> angleMapping = createMeasureMapping(Radians, Angle.class);
    private static final Mapping<Time, Double> timeMapping = createMeasureMapping(Seconds, Time.class);
    private static final Mapping<LinearVelocity, Double> linearVelocityMapping = createMeasureMapping(MetersPerSecond, LinearVelocity.class);
    private static final Mapping<AngularVelocity, Double> angularVelocityMapping = createMeasureMapping(RadiansPerSecond, AngularVelocity.class);
    private static final Mapping<Frequency, Double> frequencyMapping = createMeasureMapping(Hertz, Frequency.class);
    private static final Mapping<LinearAcceleration, Double> linearAccelerationMapping = createMeasureMapping(MetersPerSecondPerSecond, LinearAcceleration.class);
    private static final Mapping<AngularAcceleration, Double> angularAccelerationMapping = createMeasureMapping(RadiansPerSecondPerSecond, AngularAcceleration.class);
    private static final Mapping<Mass, Double> massMapping = createMeasureMapping(Kilograms, Mass.class);
    private static final Mapping<Force, Double> forceMapping = createMeasureMapping(Newtons, Force.class);
    private static final Mapping<Torque, Double> torqueMapping = createMeasureMapping(NewtonMeter, Torque.class);
    private static final Mapping<LinearMomentum, Double> linearMomentumMapping = createMeasureMapping(KilogramMetersPerSecond, LinearMomentum.class);
    private static final Mapping<AngularMomentum, Double> angularMomentumMapping = createMeasureMapping(KilogramMetersSquaredPerSecond, AngularMomentum.class);
    private static final Mapping<MomentOfInertia, Double> momentOfInertiaMapping = createMeasureMapping(KilogramSquareMeters, MomentOfInertia.class);
    private static final Mapping<Voltage, Double> voltageMapping = createMeasureMapping(Volts, Voltage.class);
    private static final Mapping<Current, Double> currentMapping = createMeasureMapping(Amps, Current.class);
    private static final Mapping<Resistance, Double> resistanceMapping = createMeasureMapping(Ohms, Resistance.class);
    private static final Mapping<Energy, Double> energyMapping = createMeasureMapping(Joules, Energy.class);
    private static final Mapping<Power, Double> powerMapping = createMeasureMapping(Watts, Power.class);
    private static final Mapping<Temperature, Double> temperatureMapping = createMeasureMapping(Celsius, Temperature.class);

    public static void registerAllMappings() {
        Mappings.registerAllMappings(
                distanceMapping,
                angleMapping,
                timeMapping,
                linearVelocityMapping,
                angularVelocityMapping,
                frequencyMapping,
                linearAccelerationMapping,
                angularAccelerationMapping,
                massMapping,
                forceMapping,
                torqueMapping,
                linearMomentumMapping,
                angularMomentumMapping,
                momentOfInertiaMapping,
                voltageMapping,
                currentMapping,
                resistanceMapping,
                energyMapping,
                powerMapping,
                temperatureMapping
        );
    }

    private static <T extends Unit, N extends Measure<T>> Mapping<N, Double> createMeasureMapping(T defaultUnit, Class<N> measureType) {
        return new Mapping<>(measureType, double.class, NetworkTableType.kDouble) {
            @Override
            public Double toNT(N startValue, Configuration config) {
                UnitConverter<T> converter = UnitConversions.initializeUnitConverter(config.getDefaultConverter(), defaultUnit);
                return converter.convertTo(startValue);
            }

            @Override
            @SuppressWarnings("unchecked") // N has to be able to cast to Measure<T> to fulfill the generic requirement
            public N toStart(Double ntValue, Configuration config) {
                UnitConverter<T> converter = UnitConversions.initializeUnitConverter(config.getDefaultConverter(), defaultUnit);
                return (N) converter.convertFrom(ntValue);
            }
        };
    }
}
