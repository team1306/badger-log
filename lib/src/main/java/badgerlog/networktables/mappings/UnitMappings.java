package badgerlog.networktables.mappings;

import edu.wpi.first.math.geometry.*;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.BaseUnits;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.measure.*;

import static edu.wpi.first.units.Units.*;

/**
 * Collection of {@link Mapping Mappings} for NetworkTables, includes all implementations of {@link Measure}, rotation, translation, twist, and pose mappings
 */
public final class UnitMappings {

    /**
     * Mapping of {@link Rotation2d} to {@link Double}
     *
     * @see RotationConfiguration
     */
    @MappingType
    public static Mapping<Rotation2d, Double> rotation2dDoubleMapping = new Mapping<>(Rotation2d.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(Rotation2d fieldValue, String config) {
            if (config == null) config = "";
            return switch (config) {
                case RotationConfiguration.ROTATIONS -> fieldValue.getRotations();
                case RotationConfiguration.RADIANS -> fieldValue.getRadians();
                default -> fieldValue.getDegrees();
            };
        }

        @Override
        public Rotation2d toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case RotationConfiguration.ROTATIONS -> Rotation2d.fromRotations(ntValue);
                case RotationConfiguration.RADIANS -> Rotation2d.fromRadians(ntValue);
                default -> Rotation2d.fromDegrees(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link Rotation3d} to {@link Double}[]
     *
     * @see RotationConfiguration
     */
    @MappingType
    public static Mapping<Rotation3d, double[]> rotation3dToDoubleArrayMapping = new Mapping<>(Rotation3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Rotation3d fieldValue, String config) {
            AngleUnit unit;
            if (config != null) unit = switch (config) {
                case RotationConfiguration.RADIANS -> Radians;
                case RotationConfiguration.ROTATIONS -> Rotations;
                default -> Degrees;
            };
            else
                unit = Degrees;

            double[] result = new double[3];
            result[0] = fieldValue.getMeasureX().in(unit);
            result[1] = fieldValue.getMeasureY().in(unit);
            result[2] = fieldValue.getMeasureZ().in(unit);
            return result;
        }

        @Override
        public Rotation3d toField(double[] ntValue, String config) {
            AngleUnit unit;
            if (config != null) unit = switch (config) {
                case RotationConfiguration.RADIANS -> Radians;
                case RotationConfiguration.ROTATIONS -> Rotations;
                default -> Degrees;
            };
            else
                unit = Degrees;
            double radianX = unit.of(ntValue[0]).in(Radians);
            double radianY = unit.of(ntValue[1]).in(Radians);
            double radianZ = unit.of(ntValue[2]).in(Radians);

            return new Rotation3d(radianX, radianY, radianZ);
        }
    };

    /**
     * Mapping of {@link Translation3d} to {@link Double}[]
     */
    @MappingType
    public static Mapping<Translation3d, double[]> translation3dToDoubleArrayMapping = new Mapping<>(Translation3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Translation3d fieldValue, String config) {
            double[] result = new double[3];
            result[0] = fieldValue.getX();
            result[1] = fieldValue.getY();
            result[2] = fieldValue.getZ();
            return result;
        }

        @Override
        public Translation3d toField(double[] ntValue, String config) {
            return new Translation3d(ntValue[0], ntValue[1], ntValue[2]);
        }
    };

    /**
     * Mapping of {@link Translation2d} to {@link Double}[]
     */
    @MappingType
    public static Mapping<Translation2d, double[]> translation2dToDoubleArrayMapping = new Mapping<>(Translation2d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Translation2d fieldValue, String config) {
            double[] result = new double[2];
            result[0] = fieldValue.getX();
            result[1] = fieldValue.getY();
            return result;
        }

        @Override
        public Translation2d toField(double[] ntValue, String config) {
            return new Translation2d(ntValue[0], ntValue[1]);
        }
    };

    /**
     * Mapping of {@link Twist2d} to {@link Double}[]
     */
    @MappingType
    public static Mapping<Twist2d, double[]> twist2dToDoubleArrayMapping = new Mapping<>(Twist2d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Twist2d fieldValue, String config) {
            double[] result = new double[3];
            result[0] = fieldValue.dx;
            result[1] = fieldValue.dy;
            result[2] = fieldValue.dtheta;
            return result;
        }

        @Override
        public Twist2d toField(double[] ntValue, String config) {
            return new Twist2d(ntValue[0], ntValue[1], ntValue[2]);
        }
    };
    /**
     * Mapping of {@link Twist3d} to {@link Double}[]
     */
    @MappingType
    public static Mapping<Twist3d, double[]> twist3dToDoubleArrayMapping = new Mapping<>(Twist3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Twist3d fieldValue, String config) {
            double[] result = new double[6];
            result[0] = fieldValue.dx;
            result[1] = fieldValue.dy;
            result[2] = fieldValue.dz;
            result[3] = fieldValue.rx;
            result[4] = fieldValue.ry;
            result[5] = fieldValue.rz;
            return result;
        }

        @Override
        public Twist3d toField(double[] ntValue, String config) {
            return new Twist3d(ntValue[0], ntValue[1], ntValue[2], ntValue[3], ntValue[4], ntValue[5]);
        }
    };

    /**
     * Mapping of {@link Pose2d} to {@link Double}[]
     */
    @MappingType
    public static Mapping<Pose2d, double[]> pose2dToDoubleArrayMapping = new Mapping<>(Pose2d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Pose2d fieldValue, String config) {
            double[] result = new double[4];
            result[0] = fieldValue.getX();
            result[1] = fieldValue.getY();
            result[2] = switch (config) {
                case RotationConfiguration.ROTATIONS -> fieldValue.getRotation().getRotations();
                case RotationConfiguration.RADIANS -> fieldValue.getRotation().getRadians();
                default -> fieldValue.getRotation().getDegrees();
            };
            
            return result;
        }

        @Override
        public Pose2d toField(double[] ntValue, String config) {
            Rotation2d rotation = switch (config) {
                case RotationConfiguration.ROTATIONS -> Rotation2d.fromRotations(ntValue[3]);
                case RotationConfiguration.RADIANS -> Rotation2d.fromRadians(ntValue[3]);
                default -> Rotation2d.fromDegrees(ntValue[3]);
            };
            
            return new Pose2d(ntValue[0], ntValue[1], rotation);
        }
    };
    /**
     * Mapping of {@link Pose3d} to {@link Double}[]
     */
    @MappingType
    public static Mapping<Pose3d, double[]> pose3dToDoubleArrayMapping = new Mapping<>(Pose3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Pose3d fieldValue, String config) {
            double[] result = new double[6];
            result[0] = fieldValue.getX();
            result[1] = fieldValue.getY();
            result[2] = fieldValue.getZ();

            AngleUnit unit;
            if (config != null) unit = switch (config) {
                case RotationConfiguration.RADIANS -> Radians;
                case RotationConfiguration.ROTATIONS -> Rotations;
                default -> Degrees;
            };
            else
                unit = Degrees;
            
            result[3] = fieldValue.getRotation().getMeasureX().in(unit);
            result[4] = fieldValue.getRotation().getMeasureY().in(unit);
            result[5] = fieldValue.getRotation().getMeasureZ().in(unit);
            
            return result;
        }

        @Override
        public Pose3d toField(double[] ntValue, String config) {
            AngleUnit unit;
            if (config != null) unit = switch (config) {
                case RotationConfiguration.RADIANS -> Radians;
                case RotationConfiguration.ROTATIONS -> Rotations;
                default -> Degrees;
            };
            else
                unit = Degrees;
            double radianX = unit.of(ntValue[3]).in(Radians);
            double radianY = unit.of(ntValue[4]).in(Radians);
            double radianZ = unit.of(ntValue[5]).in(Radians);
            
            Rotation3d rotation = new Rotation3d(radianX, radianY, radianZ);

            return new Pose3d(ntValue[0], ntValue[1], ntValue[2], rotation);
        }
    };


    /**
     * Mapping of {@link Distance} to {@link Double}
     *
     * @see DistanceConfiguration
     */
    @MappingType
    public static Mapping<Distance, Double> distanceMapping = new Mapping<>(Distance.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(Distance fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case DistanceConfiguration.INCHES -> fieldValue.in(Inches);
                case DistanceConfiguration.METERS -> fieldValue.in(Meters);
                case DistanceConfiguration.FEET -> fieldValue.in(Feet);
                case DistanceConfiguration.CENTIMETERS -> fieldValue.in(Centimeters);
                case DistanceConfiguration.MILLIMETERS -> fieldValue.in(Millimeters);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public Distance toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case DistanceConfiguration.INCHES -> Inches.of(ntValue);
                case DistanceConfiguration.METERS -> Meters.of(ntValue);
                case DistanceConfiguration.FEET -> Feet.of(ntValue);
                case DistanceConfiguration.CENTIMETERS -> Centimeters.of(ntValue);
                case DistanceConfiguration.MILLIMETERS -> Millimeters.of(ntValue);
                default -> BaseUnits.DistanceUnit.of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link Angle} to {@link Double}
     *
     * @see RotationConfiguration
     */
    @MappingType
    public static Mapping<Angle, Double> angleMapping = new Mapping<>(Angle.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(Angle fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case RotationConfiguration.DEGREES -> fieldValue.in(Degrees);
                case RotationConfiguration.ROTATIONS -> fieldValue.in(Rotations);
                case RotationConfiguration.RADIANS -> fieldValue.in(Radians);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public Angle toField(Double ntValue, String config) {
            if (config == null) config = "";
            return switch (config) {
                case RotationConfiguration.DEGREES -> Degrees.of(ntValue);
                case RotationConfiguration.ROTATIONS -> Rotations.of(ntValue);
                case RotationConfiguration.RADIANS -> Radians.of(ntValue);
                default -> BaseUnits.AngleUnit.of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link Time} to {@link Double}
     *
     * @see TimeConfiguration
     */
    @MappingType
    public static Mapping<Time, Double> timeMapping = new Mapping<>(Time.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(Time fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case TimeConfiguration.MICROSECONDS -> fieldValue.in(Microseconds);
                case TimeConfiguration.MILLISECONDS -> fieldValue.in(Milliseconds);
                case TimeConfiguration.MINUTES -> fieldValue.in(Minutes);
                case TimeConfiguration.SECONDS -> fieldValue.in(Seconds);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public Time toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case TimeConfiguration.MICROSECONDS -> Microseconds.of(ntValue);
                case TimeConfiguration.MILLISECONDS -> Milliseconds.of(ntValue);
                case TimeConfiguration.MINUTES -> Minutes.of(ntValue);
                case TimeConfiguration.SECONDS -> Seconds.of(ntValue);
                default -> BaseUnits.TimeUnit.of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link LinearVelocity} to {@link Double}
     *
     * @see LinearAccelerationConfiguration
     */
    @MappingType
    public static Mapping<LinearVelocity, Double> linearVelocityMapping = new Mapping<>(LinearVelocity.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(LinearVelocity fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case LinearVelocityConfiguration.FEET_PER_SECOND -> fieldValue.in(FeetPerSecond);
                case LinearVelocityConfiguration.INCHES_PER_SECOND -> fieldValue.in(InchesPerSecond);
                case LinearVelocityConfiguration.METERS_PER_SECOND -> fieldValue.in(MetersPerSecond);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public LinearVelocity toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case LinearVelocityConfiguration.FEET_PER_SECOND -> FeetPerSecond.of(ntValue);
                case LinearVelocityConfiguration.INCHES_PER_SECOND -> InchesPerSecond.of(ntValue);
                case LinearVelocityConfiguration.METERS_PER_SECOND -> MetersPerSecond.of(ntValue);
                default -> BaseUnits.DistanceUnit.per(Second).of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link AngularVelocity} to {@link Double}
     *
     * @see AngularVelocityConfiguration
     */
    @MappingType
    public static Mapping<AngularVelocity, Double> angularVelocityMapping = new Mapping<>(AngularVelocity.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(AngularVelocity fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case AngularVelocityConfiguration.DEGREES_PER_SECOND -> fieldValue.in(DegreesPerSecond);
                case AngularVelocityConfiguration.ROTATIONS_PER_SECOND -> fieldValue.in(RotationsPerSecond);
                case AngularVelocityConfiguration.RADIANS_PER_SECOND -> fieldValue.in(RadiansPerSecond);
                case AngularVelocityConfiguration.ROTATIONS_PER_MINUTE -> fieldValue.in(RPM);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public AngularVelocity toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case AngularVelocityConfiguration.DEGREES_PER_SECOND -> DegreesPerSecond.of(ntValue);
                case AngularVelocityConfiguration.ROTATIONS_PER_SECOND -> RotationsPerSecond.of(ntValue);
                case AngularVelocityConfiguration.RADIANS_PER_SECOND -> RadiansPerSecond.of(ntValue);
                case AngularVelocityConfiguration.ROTATIONS_PER_MINUTE -> RPM.of(ntValue);
                default -> BaseUnits.AngleUnit.per(Second).of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link Frequency} to {@link Double}
     *
     * @see FrequencyConfiguration
     */
    @MappingType
    public static Mapping<Frequency, Double> frequencyMapping = new Mapping<>(Frequency.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(Frequency fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case FrequencyConfiguration.HERTZ -> fieldValue.in(Hertz);
                case FrequencyConfiguration.MILLIHERTZ -> fieldValue.in(Millihertz);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @SuppressWarnings("DuplicateBranchesInSwitch")
        @Override
        public Frequency toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case FrequencyConfiguration.HERTZ -> Hertz.of(ntValue);
                case FrequencyConfiguration.MILLIHERTZ -> Millihertz.of(ntValue);
                default -> Hertz.of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link LinearAcceleration} to {@link Double}
     *
     * @see LinearAccelerationConfiguration
     */
    @MappingType
    public static Mapping<LinearAcceleration, Double> linearAccelerationMapping = new Mapping<>(LinearAcceleration.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(LinearAcceleration fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case LinearAccelerationConfiguration.FEET_PER_SECOND_PER_SECOND ->
                        fieldValue.in(FeetPerSecondPerSecond);
                case LinearAccelerationConfiguration.METERS_PER_SECOND_PER_SECOND ->
                        fieldValue.in(MetersPerSecondPerSecond);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public LinearAcceleration toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case LinearAccelerationConfiguration.FEET_PER_SECOND_PER_SECOND -> FeetPerSecondPerSecond.of(ntValue);
                case LinearAccelerationConfiguration.METERS_PER_SECOND_PER_SECOND ->
                        MetersPerSecondPerSecond.of(ntValue);
                default -> BaseUnits.DistanceUnit.per(Second).per(Second).of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link AngularAcceleration} to {@link Double}
     *
     * @see AngularAccelerationConfiguration
     */
    @MappingType
    public static Mapping<AngularAcceleration, Double> angularAccelerationMapping = new Mapping<>(AngularAcceleration.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(AngularAcceleration fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case AngularAccelerationConfiguration.DEGREES_PER_SECOND_PER_SECOND ->
                        fieldValue.in(DegreesPerSecondPerSecond);
                case AngularAccelerationConfiguration.RADIANS_PER_SECOND_PER_SECOND ->
                        fieldValue.in(RadiansPerSecondPerSecond);
                case AngularAccelerationConfiguration.ROTATIONS_PER_SECOND_PER_SECOND ->
                        fieldValue.in(RotationsPerSecondPerSecond);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public AngularAcceleration toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case AngularAccelerationConfiguration.DEGREES_PER_SECOND_PER_SECOND ->
                        DegreesPerSecondPerSecond.of(ntValue);
                case AngularAccelerationConfiguration.RADIANS_PER_SECOND_PER_SECOND ->
                        RadiansPerSecondPerSecond.of(ntValue);
                case AngularAccelerationConfiguration.ROTATIONS_PER_SECOND_PER_SECOND ->
                        RotationsPerSecondPerSecond.of(ntValue);
                default -> BaseUnits.AngleUnit.per(Second).per(Second).of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link Mass} to {@link Double}
     *
     * @see MassConfiguration
     */
    @MappingType
    public static Mapping<Mass, Double> massMapping = new Mapping<>(Mass.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(Mass fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case MassConfiguration.GRAMS -> fieldValue.in(Grams);
                case MassConfiguration.KILOGRAMS -> fieldValue.in(Kilograms);
                case MassConfiguration.POUNDS -> fieldValue.in(Pounds);
                case MassConfiguration.OUNCES -> fieldValue.in(Ounces);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public Mass toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case MassConfiguration.GRAMS -> Grams.of(ntValue);
                case MassConfiguration.OUNCES -> Ounces.of(ntValue);
                case MassConfiguration.KILOGRAMS -> Kilograms.of(ntValue);
                case MassConfiguration.POUNDS -> Pounds.of(ntValue);
                default -> BaseUnits.MassUnit.of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link Force} to {@link Double}
     *
     * @see ForceConfiguration
     */
    @MappingType
    public static Mapping<Force, Double> forceMapping = new Mapping<>(Force.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(Force fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case ForceConfiguration.OUNCES_FORCE -> fieldValue.in(OuncesForce);
                case ForceConfiguration.NEWTONS -> fieldValue.in(Newtons);
                case ForceConfiguration.POUNDS_FORCE -> fieldValue.in(PoundsForce);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public Force toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case ForceConfiguration.NEWTONS -> Newtons.of(ntValue);
                case ForceConfiguration.OUNCES_FORCE -> OuncesForce.of(ntValue);
                case ForceConfiguration.POUNDS_FORCE -> PoundsForce.of(ntValue);
                default -> Newtons.of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link Torque} to {@link Double}
     *
     * @see TorqueConfiguration
     */
    @MappingType
    public static Mapping<Torque, Double> torqueMapping = new Mapping<>(Torque.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(Torque fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case TorqueConfiguration.NEWTON_METERS -> fieldValue.in(NewtonMeter);
                case TorqueConfiguration.OUNCE_INCHES -> fieldValue.in(OunceInches);
                case TorqueConfiguration.POUND_INCHES -> fieldValue.in(PoundInches);
                case TorqueConfiguration.POUND_FEET -> fieldValue.in(PoundFeet);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public Torque toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case TorqueConfiguration.NEWTON_METERS -> NewtonMeter.of(ntValue);
                case TorqueConfiguration.OUNCE_INCHES -> OunceInches.of(ntValue);
                case TorqueConfiguration.POUND_FEET -> PoundFeet.of(ntValue);
                case TorqueConfiguration.POUND_INCHES -> PoundInches.of(ntValue);
                default -> NewtonMeter.of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link LinearMomentum} to {@link Double}
     *
     * @see LinearMomentumConfiguration
     */
    @MappingType
    public static Mapping<LinearMomentum, Double> linearMomentumMapping = new Mapping<>(LinearMomentum.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(LinearMomentum fieldValue, String config) {
            return fieldValue.in(KilogramMetersPerSecond);
        }

        @Override
        public LinearMomentum toField(Double ntValue, String config) {
            return KilogramMetersPerSecond.of(ntValue);
        }
    };

    /**
     * Mapping of {@link AngularMomentum} to {@link Double}
     *
     * @see AngularMomentumConfiguration
     */
    @MappingType
    public static Mapping<AngularMomentum, Double> angularMomentumMapping = new Mapping<>(AngularMomentum.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(AngularMomentum fieldValue, String config) {
            return fieldValue.in(KilogramMetersSquaredPerSecond);
        }

        @Override
        public AngularMomentum toField(Double ntValue, String config) {
            return KilogramMetersSquaredPerSecond.of(ntValue);
        }
    };

    /**
     * Mapping of {@link MomentOfInertia} to {@link Double}
     *
     * @see MomentOfInertiaConfiguration
     */
    @MappingType
    public static Mapping<MomentOfInertia, Double> momentOfInertiaMapping = new Mapping<>(MomentOfInertia.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(MomentOfInertia fieldValue, String config) {
            return fieldValue.in(KilogramSquareMeters);
        }

        @Override
        public MomentOfInertia toField(Double ntValue, String config) {
            return KilogramSquareMeters.of(ntValue);
        }
    };

    /**
     * Mapping of {@link Voltage} to {@link Double}
     *
     * @see VoltageConfiguration
     */
    @MappingType
    public static Mapping<Voltage, Double> voltageMapping = new Mapping<>(Voltage.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(Voltage fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case VoltageConfiguration.VOLTS -> fieldValue.in(Volts);
                case VoltageConfiguration.MILLIVOLTS -> fieldValue.in(Millivolts);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public Voltage toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case VoltageConfiguration.VOLTS -> Volts.of(ntValue);
                case VoltageConfiguration.MILLIVOLTS -> Millivolts.of(ntValue);
                default -> BaseUnits.VoltageUnit.of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link Current} to {@link Double}
     *
     * @see CurrentConfiguration
     */
    @MappingType
    public static Mapping<Current, Double> currentMapping = new Mapping<>(Current.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(Current fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case CurrentConfiguration.AMPS -> fieldValue.in(Amps);
                case CurrentConfiguration.MILLIAMPS -> fieldValue.in(Milliamps);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public Current toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case CurrentConfiguration.AMPS -> Amps.of(ntValue);
                case CurrentConfiguration.MILLIAMPS -> Milliamps.of(ntValue);
                default -> BaseUnits.CurrentUnit.of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link Resistance} to {@link Double}
     *
     * @see ResistanceConfiguration
     */
    @MappingType
    public static Mapping<Resistance, Double> resistanceMapping = new Mapping<>(Resistance.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(Resistance fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case ResistanceConfiguration.KILOOHM -> fieldValue.in(KiloOhm);
                case ResistanceConfiguration.MILLIOHM -> fieldValue.in(MilliOhm);
                case ResistanceConfiguration.OHMS -> fieldValue.in(Ohms);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public Resistance toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case ResistanceConfiguration.KILOOHM -> KiloOhm.of(ntValue);
                case ResistanceConfiguration.MILLIOHM -> MilliOhm.of(ntValue);
                case ResistanceConfiguration.OHMS -> Ohms.of(ntValue);
                default -> Ohms.of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link Energy} to {@link Double}
     *
     * @see EnergyConfiguration
     */
    @MappingType
    public static Mapping<Energy, Double> energyMapping = new Mapping<>(Energy.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(Energy fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case EnergyConfiguration.JOULES -> fieldValue.in(Joule);
                case EnergyConfiguration.KILOJOULE -> fieldValue.in(Kilojoule);
                case EnergyConfiguration.MILLIJOULE -> fieldValue.in(Millijoule);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public Energy toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case EnergyConfiguration.JOULES -> Joule.of(ntValue);
                case EnergyConfiguration.KILOJOULE -> Kilojoule.of(ntValue);
                case EnergyConfiguration.MILLIJOULE -> Millijoule.of(ntValue);
                default -> BaseUnits.EnergyUnit.of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link Power} to {@link Double}
     *
     * @see PowerConfiguration
     */
    @MappingType
    public static Mapping<Power, Double> powerMapping = new Mapping<>(Power.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(Power fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case PowerConfiguration.HORSEPOWER -> fieldValue.in(Horsepower);
                case PowerConfiguration.MILLIWATT -> fieldValue.in(Milliwatt);
                case PowerConfiguration.WATTS -> fieldValue.in(Watts);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public Power toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case PowerConfiguration.HORSEPOWER -> Horsepower.of(ntValue);
                case PowerConfiguration.MILLIWATT -> Milliwatt.of(ntValue);
                case PowerConfiguration.WATTS -> Watts.of(ntValue);
                default -> Watts.of(ntValue);
            };
        }
    };

    /**
     * Mapping of {@link Temperature} to {@link Double}
     *
     * @see TemperatureConfiguration
     */
    @MappingType
    public static Mapping<Temperature, Double> temperatureMapping = new Mapping<>(Temperature.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(Temperature fieldValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case TemperatureConfiguration.KELVIN -> fieldValue.in(Kelvin);
                case TemperatureConfiguration.CELSIUS -> fieldValue.in(Celsius);
                case TemperatureConfiguration.FAHRENHEIT -> fieldValue.in(Fahrenheit);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public Temperature toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case TemperatureConfiguration.KELVIN -> Kelvin.of(ntValue);
                case TemperatureConfiguration.CELSIUS -> Celsius.of(ntValue);
                case TemperatureConfiguration.FAHRENHEIT -> Fahrenheit.of(ntValue);
                default -> BaseUnits.TemperatureUnit.of(ntValue);
            };
        }
    };

    private UnitMappings() {
    }

    /**
     * Configuration options for {@link Distance} mappings
     */
    public static final class DistanceConfiguration {
        /**
         * Config option representing inches
         */
        public static final String INCHES = "inches";

        /**
         * Config option representing feet
         */
        public static final String FEET = "feet";

        /**
         * Config option representing meters
         */
        public static final String METERS = "meters";

        /**
         * Config option representing centimeters
         */
        public static final String CENTIMETERS = "centimeters";

        /**
         * Config option representing millimeters
         */
        public static final String MILLIMETERS = "millimeters";
    }

    /**
     * Configuration options for the {@link Rotation3d}, {@link Rotation2d}, and {@link Angle} mappings
     */
    public static final class RotationConfiguration {
        /**
         * Config option representing degrees
         */
        public static final String DEGREES = "degrees";

        /**
         * Config option representing radians
         */
        public static final String RADIANS = "radians";

        /**
         * Config option representing rotations
         */
        public static final String ROTATIONS = "rotations";
    }

    /**
     * Configuration options for {@link Time} mappings
     */
    public static final class TimeConfiguration {
        /**
         * Config option representing seconds
         */
        public static final String SECONDS = "seconds";

        /**
         * Config option representing minutes
         */
        public static final String MINUTES = "minutes";

        /**
         * Config option representing milliseconds
         */
        public static final String MILLISECONDS = "milliseconds";

        /**
         * Config option representing microseconds
         */
        public static final String MICROSECONDS = "microseconds";
    }

    /**
     * Configuration options for {@link LinearVelocity} mappings
     */
    public static final class LinearVelocityConfiguration {
        /**
         * Config option representing meters per second
         */
        public static final String METERS_PER_SECOND = "metersPerSecond";

        /**
         * Config option representing feet per second
         */
        public static final String FEET_PER_SECOND = "feetPerSecond";
        
        /**
         * Config option representing inches per second
         */
        public static final String INCHES_PER_SECOND = "inchesPerSecond";
    }

    /**
     * Configuration options for {@link AngularVelocity} mappings
     */
    public static final class AngularVelocityConfiguration {
        /**
         * Config option representing radians per second
         */
        public static final String RADIANS_PER_SECOND = "radiansPerSecond";
        
        /**
         * Config option representing rotations per second
         */
        public static final String ROTATIONS_PER_SECOND = "rotationsPerSecond";
        
        /**
         * Config option representing degrees per second
         */
        public static final String DEGREES_PER_SECOND = "degreesPerSecond";
        
        /**
         * Config option representing RPM
         */
        public static final String ROTATIONS_PER_MINUTE = "rotationsPerMinute";
    }

    /**
     * Configuration options for {@link Frequency} mappings
     */
    public static final class FrequencyConfiguration {
        /**
         * Config option representing hertz
         */
        public static final String HERTZ = "hertz";
        
        /**
         * Config option representing millihertz
         */
        public static final String MILLIHERTZ = "millihertz";
    }

    /**
     * Configuration options for {@link LinearAcceleration} mappings
     */
    public static final class LinearAccelerationConfiguration {
        /**
         * Config option representing meters per second squared
         */
        public static final String METERS_PER_SECOND_PER_SECOND = "metersPerSecondPerSecond";
        
        /**
         * Config option representing feet per second squared
         */
        public static final String FEET_PER_SECOND_PER_SECOND = "feetPerSecondPerSecond";
    }

    /**
     * Configuration options for {@link AngularAcceleration} mappings
     */
    public static final class AngularAccelerationConfiguration {
        /**
         * Config option representing radians per second squared
         */
        public static final String RADIANS_PER_SECOND_PER_SECOND = "radiansPerSecondPerSecond";
        
        /**
         * Config option representing rotations per second squared
         */
        public static final String ROTATIONS_PER_SECOND_PER_SECOND = "rotationsPerSecondPerSecond";
        
        /**
         * Config option representing degrees per second squared
         */
        public static final String DEGREES_PER_SECOND_PER_SECOND = "degreesPerSecondPerSecond";
    }

    /**
     * Configuration options for {@link Mass} mappings
     */
    public static final class MassConfiguration {
        /**
         * Config option representing kilograms
         */
        public static final String KILOGRAMS = "kilograms";
        
        /**
         * Config option representing grams
         */
        public static final String GRAMS = "grams";
        
        /**
         * Config option representing pounds
         */
        public static final String POUNDS = "pounds";
        
        /**
         * Config option representing ounces
         */
        public static final String OUNCES = "ounces";
    }

    /**
     * Configuration options for {@link Force} mappings
     */
    public static final class ForceConfiguration {
        /**
         * Config option representing newtons
         */
        public static final String NEWTONS = "newtons";
        
        /**
         * Config option representing pounds force
         */
        public static final String POUNDS_FORCE = "poundsForce";
        
        /**
         * Config option representing ounces force
         */
        public static final String OUNCES_FORCE = "ouncesForce";
    }

    /**
     * Configuration options for {@link Torque} mappings
     */
    public static final class TorqueConfiguration {
        /**
         * Config option representing newton meters
         */
        public static final String NEWTON_METERS = "newtonMeters";
        
        /**
         * Config option representing pound feet
         */
        public static final String POUND_FEET = "poundFeet";
        
        /**
         * Config option representing pound inches
         */
        public static final String POUND_INCHES = "poundInches";
        
        /**
         * Config option representing ounce inches
         */
        public static final String OUNCE_INCHES = "ouncesInches";
    }

    /**
     * Configuration options for {@link LinearMomentum} mappings
     */
    public static final class LinearMomentumConfiguration {
        /**
         * Config option representing kilogram meters per second
         */
        public static final String KILOGRAM_METERS_PER_SECOND = "kilogramMetersPerSecond";
    }

    /**
     * Configuration options for {@link AngularMomentum} mappings
     */
    public static final class AngularMomentumConfiguration {
        /**
         * Config option representing kilogram meters squared per second
         */
        public static final String KILOGRAM_METERS_SQUARED_PER_SECOND = "kilogramMetersSquaredPerSecond";
    }

    /**
     * Configuration options for {@link MomentOfInertia} mappings
     */
    public static final class MomentOfInertiaConfiguration {
        /**
         * Config option representing kilogram square meters
         */
        public static final String KILOGRAM_SQUARE_METERS = "kilogramSquareMeters";
    }

    /**
     * Configuration options for {@link Voltage} mappings
     */
    public static final class VoltageConfiguration {
        /**
         * Config option representing volts
         */
        public static final String VOLTS = "volts";
        
        /**
         * Config option representing millivolts
         */
        public static final String MILLIVOLTS = "millivolts";
    }

    /**
     * Configuration options for {@link Current} mappings
     */
    public static final class CurrentConfiguration {
        /**
         * Config option representing amps
         */
        public static final String AMPS = "amps";
        
        /**
         * Config option representing milliamps
         */
        public static final String MILLIAMPS = "milliamps";
    }

    /**
     * Configuration options for {@link Resistance} mappings
     */
    public static final class ResistanceConfiguration {
        /**
         * Config option representing ohms
         */
        public static final String OHMS = "ohms";
        
        /**
         * Config option representing milliohms
         */
        public static final String MILLIOHM = "milliohm";
        
        /**
         * Config option representing kiloohms
         */
        public static final String KILOOHM = "kiloohm";
    }

    /**
     * Configuration options for {@link Energy} mappings
     */
    public static final class EnergyConfiguration {
        /**
         * Config option representing joules
         */
        public static final String JOULES = "joules";
        
        /**
         * Config option representing millijoules
         */
        public static final String MILLIJOULE = "millijoule";
        
        /**
         * Config option representing kilojoule
         */
        public static final String KILOJOULE = "kilojoule";
    }

    /**
     * Configuration options for {@link Power} mappings
     */
    public static final class PowerConfiguration {
        /**
         * Config option representing watts
         */
        public static final String WATTS = "watts";
        
        /**
         * Config option representing milliwatts
         */
        public static final String MILLIWATT = "milliwatt";
        
        /**
         * Config option representing horsepower
         */
        public static final String HORSEPOWER = "horsepower";
    }

    /**
     * Configuration options for {@link Temperature} mappings
     */
    public static final class TemperatureConfiguration {
        /**
         * Config option representing Celsius
         */
        public static final String CELSIUS = "celsius";
        
        /**
         * Config option representing Fahrenheit
         */
        public static final String FAHRENHEIT = "fahrenheit";
        
        /**
         * Config option representing Kelvin
         */
        public static final String KELVIN = "kelvin";
    }
}
