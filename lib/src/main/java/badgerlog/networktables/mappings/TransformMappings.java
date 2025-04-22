package badgerlog.networktables.mappings;

import badgerlog.entry.configuration.Configuration;
import badgerlog.networktables.mappings.conversion.UnitConversions;
import badgerlog.networktables.mappings.conversion.UnitConverter;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.DistanceUnit;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;
import org.jetbrains.annotations.NotNull;

import static edu.wpi.first.units.Units.Radians;

@SuppressWarnings("DuplicatedCode")
public class TransformMappings {
    /**
     * Mapping of {@link Rotation2d} to {@link Double}
     */
    @MappingType
    public static Mapping<Rotation2d, Double> rotation2dDoubleMapping = new Mapping<>(Rotation2d.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(@NotNull Rotation2d startValue, @NotNull Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config.getDefaultConverter());

            return converter.convertTo(startValue.getMeasure());
        }

        @Override
        public Rotation2d toStart(@NotNull Double ntValue, @NotNull Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config.getDefaultConverter());

            return new Rotation2d((Angle) converter.convertFrom(ntValue));
        }
    };

    /**
     * Mapping of {@link Rotation3d} to {@link Double}[]
     */
    @MappingType
    public static Mapping<Rotation3d, double[]> rotation3dToDoubleArrayMapping = new Mapping<>(Rotation3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(@NotNull Rotation3d startValue, @NotNull Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config.getDefaultConverter());

            double[] result = new double[3];
            result[0] = converter.convertTo(startValue.getMeasureX());
            result[1] = converter.convertTo(startValue.getMeasureY());
            result[2] = converter.convertTo(startValue.getMeasureZ());
            return result;
        }

        @Override
        public Rotation3d toStart(double @NotNull [] ntValue, @NotNull Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config.getDefaultConverter());

            Angle x = (Angle) converter.convertFrom(ntValue[0]);
            Angle y = (Angle) converter.convertFrom(ntValue[1]);
            Angle z = (Angle) converter.convertFrom(ntValue[2]);
            return new Rotation3d(x, y, z);
        }
    };

    /**
     * Mapping of {@link Translation3d} to {@link Double}[]
     */
    @MappingType
    public static Mapping<Translation3d, double[]> translation3dToDoubleArrayMapping = new Mapping<>(Translation3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(@NotNull Translation3d startValue, @NotNull Configuration config) {
            UnitConverter<DistanceUnit> converter = UnitConversions.initializeDistanceConverter(config.getDefaultConverter());

            double[] result = new double[3];
            result[0] = converter.convertTo(startValue.getMeasureX());
            result[1] = converter.convertTo(startValue.getMeasureY());
            result[2] = converter.convertTo(startValue.getMeasureZ());
            return result;
        }

        @Override
        public Translation3d toStart(double @NotNull [] ntValue, @NotNull Configuration config) {
            UnitConverter<DistanceUnit> converter = UnitConversions.initializeDistanceConverter(config.getDefaultConverter());

            Distance x = (Distance) converter.convertFrom(ntValue[0]);
            Distance y = (Distance) converter.convertFrom(ntValue[1]);
            Distance z = (Distance) converter.convertFrom(ntValue[2]);
            return new Translation3d(x, y, z);
        }
    };

    /**
     * Mapping of {@link Translation2d} to {@link Double}[]
     */
    @MappingType
    public static Mapping<Translation2d, double[]> translation2dToDoubleArrayMapping = new Mapping<>(Translation2d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(@NotNull Translation2d startValue, @NotNull Configuration config) {
            UnitConverter<DistanceUnit> converter = UnitConversions.initializeDistanceConverter(config.getDefaultConverter());

            double[] result = new double[2];
            result[0] = converter.convertTo(startValue.getMeasureX());
            result[1] = converter.convertTo(startValue.getMeasureY());
            return result;
        }

        @Override
        public Translation2d toStart(double @NotNull [] ntValue, @NotNull Configuration config) {
            UnitConverter<DistanceUnit> converter = UnitConversions.initializeDistanceConverter(config.getDefaultConverter());

            Distance x = (Distance) converter.convertFrom(ntValue[0]);
            Distance y = (Distance) converter.convertFrom(ntValue[1]);
            return new Translation2d(x, y);
        }
    };

    /**
     * Mapping of {@link Twist2d} to {@link Double}[]
     */
    @MappingType
    public static Mapping<Twist2d, double[]> twist2dToDoubleArrayMapping = new Mapping<>(Twist2d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(@NotNull Twist2d startValue, @NotNull Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config.getDefaultConverter());

            double[] result = new double[3];
            result[0] = startValue.dx;
            result[1] = startValue.dy;
            result[2] = converter.convertTo(Radians.of(startValue.dtheta));
            return result;
        }

        @Override
        public Twist2d toStart(double @NotNull [] ntValue, @NotNull Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config.getDefaultConverter());

            return new Twist2d(ntValue[0], ntValue[1], converter.convertFrom(ntValue[3]).in(Radians));
        }
    };
    /**
     * Mapping of {@link Twist3d} to {@link Double}[]
     */
    @MappingType
    public static Mapping<Twist3d, double[]> twist3dToDoubleArrayMapping = new Mapping<>(Twist3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(@NotNull Twist3d startValue, @NotNull Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config.getDefaultConverter());

            double[] result = new double[6];
            result[0] = startValue.dx;
            result[1] = startValue.dy;
            result[2] = startValue.dz;

            result[3] = converter.convertTo(Radians.of(startValue.rx));
            result[4] = converter.convertTo(Radians.of(startValue.ry));
            result[5] = converter.convertTo(Radians.of(startValue.rz));
            return result;
        }

        @Override
        public Twist3d toStart(double @NotNull [] ntValue, @NotNull Configuration config) {
            UnitConverter<AngleUnit> converter = UnitConversions.initializeRotationConverter(config.getDefaultConverter());

            double dx = ntValue[0], dy = ntValue[1], dz = ntValue[2];

            double rx = converter.convertFrom(ntValue[3]).in(Radians);
            double ry = converter.convertFrom(ntValue[4]).in(Radians);
            double rz = converter.convertFrom(ntValue[5]).in(Radians);

            return new Twist3d(dx, dy, dz, rx, ry, rz);
        }
    };

    /**
     * Mapping of {@link Pose2d} to {@link Double}[]
     */
    @MappingType
    public static Mapping<Pose2d, double[]> pose2dToDoubleArrayMapping = new Mapping<>(Pose2d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(@NotNull Pose2d startValue, @NotNull Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config.getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config.getConverter("rotation"));

            double[] result = new double[3];
            result[0] = translationConverter.convertTo(startValue.getMeasureX());
            result[1] = translationConverter.convertTo(startValue.getMeasureY());
            result[2] = rotationConverter.convertTo(startValue.getRotation().getMeasure());

            return result;
        }

        @Override
        public Pose2d toStart(double @NotNull [] ntValue, @NotNull Configuration config) {
            UnitConverter<DistanceUnit> translationConverter = UnitConversions.initializeDistanceConverter(config.getConverter("translation"));
            UnitConverter<AngleUnit> rotationConverter = UnitConversions.initializeRotationConverter(config.getConverter("rotation"));

            Distance x = (Distance) translationConverter.convertFrom(ntValue[0]);
            Distance y = (Distance) translationConverter.convertFrom(ntValue[1]);

            Angle rotation = (Angle) rotationConverter.convertFrom(ntValue[2]);

            return new Pose2d(x, y, new Rotation2d(rotation));
        }
    };
    /**
     * Mapping of {@link Pose3d} to {@link Double}[]
     */
    @MappingType
    public static Mapping<Pose3d, double[]> pose3dToDoubleArrayMapping = new Mapping<>(Pose3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(@NotNull Pose3d startValue, @NotNull Configuration config) {
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
        public Pose3d toStart(double @NotNull [] ntValue, @NotNull Configuration config) {
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
}
