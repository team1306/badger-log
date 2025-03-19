package frc.robot.util.dashboardv3.networktables.mappings;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.BaseUnits;
import edu.wpi.first.units.measure.Distance;

import static edu.wpi.first.units.Units.*;
import static frc.robot.util.dashboardv3.networktables.mappings.UnitMappings.DistanceConfiguration.INCHES;
import static frc.robot.util.dashboardv3.networktables.mappings.UnitMappings.DistanceConfiguration.METERS;
import static frc.robot.util.dashboardv3.networktables.mappings.UnitMappings.RotationConfiguration.RADIANS;
import static frc.robot.util.dashboardv3.networktables.mappings.UnitMappings.RotationConfiguration.ROTATIONS;

public class UnitMappings {
    @MappingType
    public static Mapping<Distance, Double> distanceMapping = new Mapping<>(Distance.class, double.class, NetworkTableType.kDouble) {
        @Override
        public Double toNT(Distance fieldValue, String config) {
            if(config == null) config = "";

            return switch (config) {
                case INCHES -> fieldValue.in(Inches);
                case METERS -> fieldValue.in(Meters);
                default -> fieldValue.baseUnitMagnitude();
            };
        }

        @Override
        public Distance toField(Double ntValue, String config) {
            if(config == null) config = "";

            return switch (config) {
                case INCHES -> Inches.of(ntValue);
                case METERS -> Meters.of(ntValue);
                default -> Distance.ofBaseUnits(ntValue, BaseUnits.DistanceUnit);
            };
        }
    };
    
    public static class DistanceConfiguration{
        public static final String INCHES = "inches";
        public static final String METERS = "meters";
    }
    
    @MappingType
    public static Mapping<Rotation2d, Double> rotation2dDoubleMapping = new Mapping<>(Rotation2d.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(Rotation2d fieldValue, String config) {
            if(config == null) config = "";
            return switch (config) {
                case ROTATIONS -> fieldValue.getRotations();
                case RADIANS -> fieldValue.getRadians();
                default -> fieldValue.getDegrees();
            };
        }

        @Override
        public Rotation2d toField(Double ntValue, String config) {
            if(config == null) config = "";

            return switch (config) {
                case ROTATIONS -> Rotation2d.fromRotations(ntValue);
                case RADIANS -> Rotation2d.fromRadians(ntValue);
                default -> Rotation2d.fromDegrees(ntValue);
            };
        }
    };


    public static class RotationConfiguration{
        public static final String DEGREES = "degrees";
        public static final String RADIANS = "radians";
        public static final String ROTATIONS = "rotations";
    }

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

    @MappingType
    public static Mapping<Rotation3d, double[]> rotation3dToDoubleArrayMapping = new Mapping<>(Rotation3d.class, double[].class, NetworkTableType.kDoubleArray) {
        @Override
        public double[] toNT(Rotation3d fieldValue, String config) {
            AngleUnit unit;
            if(config != null) unit = switch (config) {
                case RADIANS -> Radians;
                case ROTATIONS -> Rotations;
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
            if(config != null) unit = switch (config) {
                case RADIANS -> Radians;
                case ROTATIONS -> Rotations;
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
}
