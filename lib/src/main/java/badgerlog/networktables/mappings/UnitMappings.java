package badgerlog.networktables.mappings;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.BaseUnits;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.measure.Distance;

import static edu.wpi.first.units.Units.*;

/**
 * Collection of {@link Mapping Mappings} for NetworkTables, includes the {@link Measure}, rotation, translation, twist, and pose mappings
 */
//TODO Finish all the mappings and add all the configuration options for measures
public final class UnitMappings {
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
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public Distance toField(Double ntValue, String config) {
            if (config == null) config = "";

            return switch (config) {
                case DistanceConfiguration.INCHES -> Inches.of(ntValue);
                case DistanceConfiguration.METERS -> Meters.of(ntValue);
                default -> Distance.ofBaseUnits(ntValue, BaseUnits.DistanceUnit);
            };
        }
    };
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
    private UnitMappings() {
    }

    /**
     * Configuration options for {@link Distance} mappings
     */
    public static class DistanceConfiguration {
        /**
         * Config option representing inches
         */
        public static final String INCHES = "inches";
        /**
         * Config option representing meters
         */
        public static final String METERS = "meters";
    }

    /**
     * Configuration options for the {@link Rotation3d} and {@link Rotation2d} mappings
     */
    public static class RotationConfiguration {
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
}
