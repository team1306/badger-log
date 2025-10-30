package badgerlog.conversion.internal;

import badgerlog.annotations.configuration.Configuration;
import badgerlog.conversion.Mapping;
import badgerlog.conversion.Mappings;
import badgerlog.conversion.UnitConversions;
import badgerlog.conversion.UnitConverter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.geometry.Twist3d;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.DistanceUnit;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Radians;

/**
 * Implementations of all the mappings that are common WPILib transform classes.
 */
@SuppressWarnings("DuplicatedCode")
public final class TransformMappings {

    private static final Mapping<Rotation2d, Double> rotation2dDoubleMapping = new Mapping<>(Rotation2d.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(Rotation2d startValue, Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config
                    .getDefaultConverter());

            return converter.convertTo(startValue.getMeasure());
        }

        @Override
        public Rotation2d toStart(Double ntValue, Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config
                    .getDefaultConverter());

            return new Rotation2d((Angle) converter.convertFrom(ntValue));
        }
    };
    private static final Mapping<Rotation3d, double[]> rotation3dToDoubleArrayMapping = new Mapping<>(Rotation3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Rotation3d startValue, Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config
                    .getDefaultConverter());

            double[] result = new double[3];
            result[0] = converter.convertTo(startValue.getMeasureX());
            result[1] = converter.convertTo(startValue.getMeasureY());
            result[2] = converter.convertTo(startValue.getMeasureZ());
            return result;
        }

        @Override
        public Rotation3d toStart(double[] ntValue, Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config
                    .getDefaultConverter());

            Angle x = (Angle) converter.convertFrom(ntValue[0]);
            Angle y = (Angle) converter.convertFrom(ntValue[1]);
            Angle z = (Angle) converter.convertFrom(ntValue[2]);
            return new Rotation3d(x, y, z);
        }
    };
    private static final Mapping<Translation2d, double[]> translation2dToDoubleArrayMapping = new Mapping<>(Translation2d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Translation2d startValue, Configuration config) {
            UnitConverter<DistanceUnit> converter = UnitConversions.initializeDistanceConverter(config
                    .getDefaultConverter());

            double[] result = new double[2];
            result[0] = converter.convertTo(startValue.getMeasureX());
            result[1] = converter.convertTo(startValue.getMeasureY());
            return result;
        }

        @Override
        public Translation2d toStart(double[] ntValue, Configuration config) {
            UnitConverter<DistanceUnit> converter = UnitConversions.initializeDistanceConverter(config
                    .getDefaultConverter());

            Distance x = (Distance) converter.convertFrom(ntValue[0]);
            Distance y = (Distance) converter.convertFrom(ntValue[1]);
            return new Translation2d(x, y);
        }
    };
    private static final Mapping<Translation3d, double[]> translation3dToDoubleArrayMapping = new Mapping<>(Translation3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Translation3d startValue, Configuration config) {
            UnitConverter<DistanceUnit> converter = UnitConversions.initializeDistanceConverter(config
                    .getDefaultConverter());

            double[] result = new double[3];
            result[0] = converter.convertTo(startValue.getMeasureX());
            result[1] = converter.convertTo(startValue.getMeasureY());
            result[2] = converter.convertTo(startValue.getMeasureZ());
            return result;
        }

        @Override
        public Translation3d toStart(double[] ntValue, Configuration config) {
            UnitConverter<DistanceUnit> converter = UnitConversions.initializeDistanceConverter(config
                    .getDefaultConverter());

            Distance x = (Distance) converter.convertFrom(ntValue[0]);
            Distance y = (Distance) converter.convertFrom(ntValue[1]);
            Distance z = (Distance) converter.convertFrom(ntValue[2]);
            return new Translation3d(x, y, z);
        }
    };
    private static final Mapping<Twist2d, double[]> twist2dToDoubleArrayMapping = new Mapping<>(Twist2d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Twist2d startValue, Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config
                    .getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config
                    .getConverter("rotation"));

            double[] result = new double[3];
            result[0] = translationConverter.convertTo(Meters.of(startValue.dx));
            result[1] = translationConverter.convertTo(Meters.of(startValue.dy));
            result[2] = rotationConverter.convertTo(Radians.of(startValue.dtheta));
            return result;
        }

        @Override
        public Twist2d toStart(double[] ntValue, Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config
                    .getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config
                    .getConverter("rotation"));

            return new Twist2d(
                    translationConverter.convertFrom(ntValue[0]).in(Meters), translationConverter
                            .convertFrom(ntValue[1])
                            .in(Meters), rotationConverter.convertFrom(ntValue[2])
                                    .in(Radians)
            );
        }
    };
    private static final Mapping<Twist3d, double[]> twist3dToDoubleArrayMapping = new Mapping<>(Twist3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Twist3d startValue, Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config
                    .getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config
                    .getConverter("rotation"));

            double[] result = new double[6];
            result[0] = translationConverter.convertTo(Meters.of(startValue.dx));
            result[1] = translationConverter.convertTo(Meters.of(startValue.dy));
            result[2] = translationConverter.convertTo(Meters.of(startValue.dz));

            result[3] = rotationConverter.convertTo(Radians.of(startValue.rx));
            result[4] = rotationConverter.convertTo(Radians.of(startValue.ry));
            result[5] = rotationConverter.convertTo(Radians.of(startValue.rz));
            return result;
        }

        @Override
        public Twist3d toStart(double[] ntValue, Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config
                    .getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config
                    .getConverter("rotation"));

            double dx = translationConverter.convertFrom(ntValue[0]).in(Meters);
            double dy = translationConverter.convertFrom(ntValue[1]).in(Meters);
            double dz = translationConverter.convertFrom(ntValue[2]).in(Meters);

            double rx = rotationConverter.convertFrom(ntValue[3]).in(Radians);
            double ry = rotationConverter.convertFrom(ntValue[4]).in(Radians);
            double rz = rotationConverter.convertFrom(ntValue[5]).in(Radians);

            return new Twist3d(dx, dy, dz, rx, ry, rz);
        }
    };
    private static final Mapping<Pose2d, double[]> pose2dToDoubleArrayMapping = new Mapping<>(Pose2d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Pose2d startValue, Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config
                    .getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config
                    .getConverter("rotation"));

            double[] result = new double[3];
            result[0] = translationConverter.convertTo(startValue.getMeasureX());
            result[1] = translationConverter.convertTo(startValue.getMeasureY());
            result[2] = rotationConverter.convertTo(startValue.getRotation().getMeasure());

            return result;
        }

        @Override
        public Pose2d toStart(double[] ntValue, Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config
                    .getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config
                    .getConverter("rotation"));

            Distance x = (Distance) translationConverter.convertFrom(ntValue[0]);
            Distance y = (Distance) translationConverter.convertFrom(ntValue[1]);

            Angle rotation = (Angle) rotationConverter.convertFrom(ntValue[2]);

            return new Pose2d(x, y, new Rotation2d(rotation));
        }
    };
    private static final Mapping<Pose3d, double[]> pose3dToDoubleArrayMapping = new Mapping<>(Pose3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Pose3d startValue, Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config
                    .getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config
                    .getConverter("rotation"));

            double[] result = new double[6];
            result[0] = translationConverter.convertTo(startValue.getMeasureX());
            result[1] = translationConverter.convertTo(startValue.getMeasureY());
            result[2] = translationConverter.convertTo(startValue.getMeasureZ());

            result[3] = rotationConverter.convertTo(startValue.getRotation().getMeasureX());
            result[4] = rotationConverter.convertTo(startValue.getRotation().getMeasureY());
            result[5] = rotationConverter.convertTo(startValue.getRotation().getMeasureZ());

            return result;
        }

        @Override
        public Pose3d toStart(double[] ntValue, Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config
                    .getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config
                    .getConverter("rotation"));

            Distance x = (Distance) translationConverter.convertFrom(ntValue[0]);
            Distance y = (Distance) translationConverter.convertFrom(ntValue[1]);
            Distance z = (Distance) translationConverter.convertFrom(ntValue[2]);

            Angle rotationX = (Angle) rotationConverter.convertFrom(ntValue[3]);
            Angle rotationY = (Angle) rotationConverter.convertFrom(ntValue[4]);
            Angle rotationZ = (Angle) rotationConverter.convertFrom(ntValue[5]);

            return new Pose3d(new Translation3d(x, y, z), new Rotation3d(rotationX, rotationY, rotationZ));
        }
    };

    private TransformMappings() {
    }

    /**
     * Registers all mappings in this class.
     */
    public static void registerAllMappings() {
        Mappings.registerAllMappings(
                rotation2dDoubleMapping, rotation3dToDoubleArrayMapping, translation2dToDoubleArrayMapping, translation3dToDoubleArrayMapping, twist2dToDoubleArrayMapping, twist3dToDoubleArrayMapping, pose2dToDoubleArrayMapping, pose3dToDoubleArrayMapping
        );
    }
}
