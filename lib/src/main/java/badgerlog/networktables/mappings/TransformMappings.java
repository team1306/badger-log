package badgerlog.networktables.mappings;

import badgerlog.annotations.configuration.Configuration;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.DistanceUnit;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;

import javax.annotation.Nonnull;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Radians;

/**
 * Collection of predefined {@link Mapping} implementations for converting geometry classes to/from {@code double} and {@code double[]} values.
 */
@SuppressWarnings("DuplicatedCode")
public final class TransformMappings {
    /**
     * Maps {@link Rotation2d} to a Double representing the angle.
     * Converted using {@link UnitConversions#initializeRotationConverter}.
     */
    @MappingType
    public static Mapping<Rotation2d, Double> rotation2dDoubleMapping = new Mapping<>(Rotation2d.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(@Nonnull Rotation2d startValue, @Nonnull Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config.getDefaultConverter());

            return converter.convertTo(startValue.getMeasure());
        }

        @Override
        public Rotation2d toStart(@Nonnull Double ntValue, @Nonnull Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config.getDefaultConverter());

            return new Rotation2d((Angle) converter.convertFrom(ntValue));
        }
    };

    /**
     * Maps {@link Rotation3d} to a double[3] array: [X-axis rotation, Y-axis rotation, Z-axis rotation].
     * Converted using {@link UnitConversions#initializeRotationConverter}.
     */
    @MappingType
    public static Mapping<Rotation3d, double[]> rotation3dToDoubleArrayMapping = new Mapping<>(Rotation3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(@Nonnull Rotation3d startValue, @Nonnull Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config.getDefaultConverter());

            double[] result = new double[3];
            result[0] = converter.convertTo(startValue.getMeasureX());
            result[1] = converter.convertTo(startValue.getMeasureY());
            result[2] = converter.convertTo(startValue.getMeasureZ());
            return result;
        }

        @Override
        public Rotation3d toStart(@Nonnull double[] ntValue, @Nonnull Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config.getDefaultConverter());

            Angle x = (Angle) converter.convertFrom(ntValue[0]);
            Angle y = (Angle) converter.convertFrom(ntValue[1]);
            Angle z = (Angle) converter.convertFrom(ntValue[2]);
            return new Rotation3d(x, y, z);
        }
    };

    /**
     * Maps {@link Translation3d} to a double[3] array: [X translation, Y translation, Z translation].
     * Converted using {@link UnitConversions#initializeDistanceConverter}.
     */
    @MappingType
    public static Mapping<Translation3d, double[]> translation3dToDoubleArrayMapping = new Mapping<>(Translation3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(@Nonnull Translation3d startValue, @Nonnull Configuration config) {
            UnitConverter<DistanceUnit> converter = UnitConversions.initializeDistanceConverter(config.getDefaultConverter());

            double[] result = new double[3];
            result[0] = converter.convertTo(startValue.getMeasureX());
            result[1] = converter.convertTo(startValue.getMeasureY());
            result[2] = converter.convertTo(startValue.getMeasureZ());
            return result;
        }

        @Override
        public Translation3d toStart(@Nonnull double[] ntValue, @Nonnull Configuration config) {
            UnitConverter<DistanceUnit> converter = UnitConversions.initializeDistanceConverter(config.getDefaultConverter());

            Distance x = (Distance) converter.convertFrom(ntValue[0]);
            Distance y = (Distance) converter.convertFrom(ntValue[1]);
            Distance z = (Distance) converter.convertFrom(ntValue[2]);
            return new Translation3d(x, y, z);
        }
    };

    /**
     * Maps {@link Translation2d} to a double[2] array: [X translation, Y translation].
     * Converted using {@link UnitConversions#initializeDistanceConverter}.
     */
    @MappingType
    public static Mapping<Translation2d, double[]> translation2dToDoubleArrayMapping = new Mapping<>(Translation2d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(@Nonnull Translation2d startValue, @Nonnull Configuration config) {
            UnitConverter<DistanceUnit> converter = UnitConversions.initializeDistanceConverter(config.getDefaultConverter());

            double[] result = new double[2];
            result[0] = converter.convertTo(startValue.getMeasureX());
            result[1] = converter.convertTo(startValue.getMeasureY());
            return result;
        }

        @Override
        public Translation2d toStart(@Nonnull double[] ntValue, @Nonnull Configuration config) {
            UnitConverter<DistanceUnit> converter = UnitConversions.initializeDistanceConverter(config.getDefaultConverter());

            Distance x = (Distance) converter.convertFrom(ntValue[0]);
            Distance y = (Distance) converter.convertFrom(ntValue[1]);
            return new Translation2d(x, y);
        }
    };

    /**
     * Maps {@link Twist2d} to a double[3] array in the order:
     * [X translation, Y translation, theta rotation].
     * <p>
     * Linear components (dx/dy) use the "translation" {@link UnitConverter}.
     * Angular component (dtheta) uses the "rotation" {@link UnitConverter}.
     */
    @MappingType
    public static Mapping<Twist2d, double[]> twist2dToDoubleArrayMapping = new Mapping<>(Twist2d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(@Nonnull Twist2d startValue, @Nonnull Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config.getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config.getConverter("rotation"));

            double[] result = new double[3];
            result[0] = translationConverter.convertTo(Meters.of(startValue.dx));
            result[1] = translationConverter.convertTo(Meters.of(startValue.dy));
            result[2] = rotationConverter.convertTo(Radians.of(startValue.dtheta));
            return result;
        }

        @Override
        public Twist2d toStart(@Nonnull double[] ntValue, @Nonnull Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config.getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config.getConverter("rotation"));

            return new Twist2d(
                    translationConverter.convertFrom(ntValue[0]).in(Meters),
                    translationConverter.convertFrom(ntValue[1]).in(Meters),
                    rotationConverter.convertFrom(ntValue[2]).in(Radians)
            );
        }
    };

    /**
     * Maps {@link Twist3d} to a double[6] array in the order:
     * [X translation, Y translation, Z translation,
     * X-axis rotation, Y-axis rotation, Z-axis rotation].
     * <p>
     * Linear components (dx/dy/dz) use the "translation" {@link UnitConverter}.
     * Angular components (rx/ry/rz) use the "rotation" {@link UnitConverter}.
     */
    @MappingType
    public static Mapping<Twist3d, double[]> twist3dToDoubleArrayMapping = new Mapping<>(Twist3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(@Nonnull Twist3d startValue, @Nonnull Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config.getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config.getConverter("rotation"));

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
        public Twist3d toStart(@Nonnull double[] ntValue, @Nonnull Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config.getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config.getConverter("rotation"));

            double dx = translationConverter.convertFrom(ntValue[0]).in(Meters);
            double dy = translationConverter.convertFrom(ntValue[1]).in(Meters);
            double dz = translationConverter.convertFrom(ntValue[2]).in(Meters);

            double rx = rotationConverter.convertFrom(ntValue[3]).in(Radians);
            double ry = rotationConverter.convertFrom(ntValue[4]).in(Radians);
            double rz = rotationConverter.convertFrom(ntValue[5]).in(Radians);

            return new Twist3d(dx, dy, dz, rx, ry, rz);
        }
    };

    /**
     * Maps {@link Pose2d} to a double[3] array: [X translation, Y translation, theta rotation].
     * <p>
     * Linear components use the "translation" {@link UnitConverter}.
     * Angular component use the "rotation" {@link UnitConverter}.
     */
    @MappingType
    public static Mapping<Pose2d, double[]> pose2dToDoubleArrayMapping = new Mapping<>(Pose2d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(@Nonnull Pose2d startValue, @Nonnull Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config.getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config.getConverter("rotation"));

            double[] result = new double[3];
            result[0] = translationConverter.convertTo(startValue.getMeasureX());
            result[1] = translationConverter.convertTo(startValue.getMeasureY());
            result[2] = rotationConverter.convertTo(startValue.getRotation().getMeasure());

            return result;
        }

        @Override
        public Pose2d toStart(@Nonnull double[] ntValue, @Nonnull Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config.getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config.getConverter("rotation"));

            Distance x = (Distance) translationConverter.convertFrom(ntValue[0]);
            Distance y = (Distance) translationConverter.convertFrom(ntValue[1]);

            Angle rotation = (Angle) rotationConverter.convertFrom(ntValue[2]);

            return new Pose2d(x, y, new Rotation2d(rotation));
        }
    };

    /**
     * Maps {@link Pose3d} to a double[6] array:
     * [X translation, Y translation, Z translation, X-axis rotation, Y-axis rotation, Z-axis rotation].
     * <p>
     * Linear components use the "translation" {@link UnitConverter}.
     * Angular components use the "rotation" {@link UnitConverter}.
     */
    @MappingType
    public static Mapping<Pose3d, double[]> pose3dToDoubleArrayMapping = new Mapping<>(Pose3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(@Nonnull Pose3d startValue, @Nonnull Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config.getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config.getConverter("rotation"));

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
        public Pose3d toStart(@Nonnull double[] ntValue, @Nonnull Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config.getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config.getConverter("rotation"));

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
}
