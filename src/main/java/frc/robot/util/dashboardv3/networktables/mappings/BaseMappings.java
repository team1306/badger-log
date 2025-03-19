package frc.robot.util.dashboardv3.networktables.mappings;

import edu.wpi.first.networktables.NetworkTableType;

/**
 * The base mappings for NetworkTables, includes only double, boolean, integer, and string mappings
 */
public class BaseMappings {

    @MappingType
    public static Mapping<Double, Double> doubleMapping = new Mapping<>(double.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(Double fieldValue, String config) {
            return fieldValue;
        }

        @Override
        public Double toField(Double ntValue, String config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<Double, Double> double1Mapping = new Mapping<>(Double.class, Double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(Double fieldValue, String config) {
            return fieldValue;
        }

        @Override
        public Double toField(Double ntValue, String config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<Boolean, Boolean> booleanMapping = new Mapping<>(boolean.class, boolean.class, NetworkTableType.kBoolean) {

        @Override
        public Boolean toNT(Boolean fieldValue, String config) {
            return fieldValue;
        }

        @Override
        public Boolean toField(Boolean ntValue, String config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<Boolean, Boolean> boolean1Mapping = new Mapping<>(Boolean.class, Boolean.class, NetworkTableType.kBoolean) {

        @Override
        public Boolean toNT(Boolean fieldValue, String config) {
            return fieldValue;
        }

        @Override
        public Boolean toField(Boolean ntValue, String config) {
            return ntValue;
        }
    };
    
    @MappingType
    public static Mapping<Integer, Double> integerMapping = new Mapping<>(int.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(Integer fieldValue, String config) {
            return fieldValue.doubleValue();
        }

        @Override
        public Integer toField(Double ntValue, String config) {
            return ntValue.intValue();
        }
    };

    @MappingType
    public static Mapping<Integer, Double> integer1Mapping = new Mapping<>(Integer.class, Double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(Integer fieldValue, String config) {
            return fieldValue.doubleValue();
        }

        @Override
        public Integer toField(Double ntValue, String config) {
            return ntValue.intValue();
        }
    };
    
    @MappingType
    public static Mapping<String, String> stringMapping = new Mapping<>(String.class, String.class, NetworkTableType.kString) {

        @Override
        public String toNT(String fieldValue, String config) {
            return fieldValue;
        }

        @Override
        public String toField(String ntValue, String config) {
            return ntValue;
        }
    };
}
