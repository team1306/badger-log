package badgerlog.networktables.mappings;

import badgerlog.entry.configuration.Configuration;
import edu.wpi.first.networktables.NetworkTableType;
import org.jetbrains.annotations.NotNull;

/**
 * Collection of {@link Mapping Mappings} for NetworkTables, includes only {@link Double}, {@link Boolean}, {@link Integer}, and {@link String} mappings
 */
public final class BaseMappings {
    
    @MappingType
    public static Mapping<Double, Double> doubleMapping = new Mapping<>(double.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(@NotNull Double startValue, @NotNull Configuration config) {
            return startValue;
        }

        @Override
        public Double toStart(@NotNull Double ntValue, @NotNull Configuration config) {
            return ntValue;
        }
    };
    
    @MappingType
    public static Mapping<Double, Double> double1Mapping = new Mapping<>(Double.class, Double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(@NotNull Double startValue, @NotNull Configuration config) {
            return startValue;
        }

        @Override
        public Double toStart(@NotNull Double ntValue, @NotNull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<Float, Float> floatMapping = new Mapping<>(float.class, float.class, NetworkTableType.kFloat) {

        @Override
        public Float toNT(@NotNull Float startValue, @NotNull Configuration config) {
            return startValue;
        }

        @Override
        public Float toStart(@NotNull Float ntValue, @NotNull Configuration config) {
            return ntValue;
        }
    };
 
    @MappingType
    public static Mapping<Float, Float> float1Mapping = new Mapping<>(Float.class, Float.class, NetworkTableType.kFloat) {

        @Override
        public Float toNT(@NotNull Float startValue, @NotNull Configuration config) {
            return startValue;
        }

        @Override
        public Float toStart(@NotNull Float ntValue, @NotNull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<Boolean, Boolean> booleanMapping = new Mapping<>(boolean.class, boolean.class, NetworkTableType.kBoolean) {

        @Override
        public Boolean toNT(@NotNull Boolean startValue, @NotNull Configuration config) {
            return startValue;
        }

        @Override
        public Boolean toStart(@NotNull Boolean ntValue, @NotNull Configuration config) {
            return ntValue;
        }
    };
  
    @MappingType
    public static Mapping<Boolean, Boolean> boolean1Mapping = new Mapping<>(Boolean.class, Boolean.class, NetworkTableType.kBoolean) {

        @Override
        public Boolean toNT(@NotNull Boolean startValue, @NotNull Configuration config) {
            return startValue;
        }

        @Override
        public Boolean toStart(@NotNull Boolean ntValue, @NotNull Configuration config) {
            return ntValue;
        }
    };
   
    @MappingType
    public static Mapping<Integer, Integer> integerMapping = new Mapping<>(int.class, int.class, NetworkTableType.kInteger) {

        @Override
        public Integer toNT(@NotNull Integer startValue, @NotNull Configuration config) {
            return startValue;
        }

        @Override
        public Integer toStart(@NotNull Integer ntValue, @NotNull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<Integer, Integer> integer1Mapping = new Mapping<>(Integer.class, Integer.class, NetworkTableType.kInteger) {

        @Override
        public Integer toNT(@NotNull Integer startValue, @NotNull Configuration config) {
            return startValue;
        }

        @Override
        public Integer toStart(@NotNull Integer ntValue, @NotNull Configuration config) {
            return ntValue;
        }
    };
    
    @MappingType
    public static Mapping<String, String> stringMapping = new Mapping<>(String.class, String.class, NetworkTableType.kString) {

        @Override
        public String toNT(@NotNull String startValue, @NotNull Configuration config) {
            return startValue;
        }

        @Override
        public String toStart(@NotNull String ntValue, @NotNull Configuration config) {
            return ntValue;
        }
    };
    
    @MappingType
    public static Mapping<double[], double[]> doubleArrayMapping = new Mapping<>(double[].class, double[].class, NetworkTableType.kDoubleArray) {

        @Override
        public double[] toNT(double @NotNull [] startValue, @NotNull Configuration config) {
            return startValue;
        }

        @Override
        public double[] toStart(double @NotNull [] ntValue, @NotNull Configuration config) {
            return ntValue;
        }
    };
    
    @MappingType
    public static Mapping<boolean[], boolean[]> booleanArrayMapping = new Mapping<>(boolean[].class, boolean[].class, NetworkTableType.kBooleanArray) {

        @Override
        public boolean[] toNT(boolean @NotNull [] startValue, @NotNull Configuration config) {
            return startValue;
        }

        @Override
        public boolean[] toStart(boolean @NotNull [] ntValue, @NotNull Configuration config) {
            return ntValue;
        }
    };
    
    @MappingType
    public static Mapping<String[], String[]> stringArrayMapping = new Mapping<>(String[].class, String[].class, NetworkTableType.kStringArray) {

        @Override
        public String[] toNT(String @NotNull [] startValue, @NotNull Configuration config) {
            return startValue;
        }

        @Override
        public String[] toStart(String @NotNull [] ntValue, @NotNull Configuration config) {
            return ntValue;
        }
    };
    
    @MappingType
    public static Mapping<float[], float[]> floatArrayMapping = new Mapping<>(float[].class, float[].class, NetworkTableType.kFloatArray) {

        @Override
        public float[] toNT(float @NotNull [] startValue, @NotNull Configuration config) {
            return startValue;
        }

        @Override
        public float[] toStart(float @NotNull [] ntValue, @NotNull Configuration config) {
            return ntValue;
        }
    };

    private BaseMappings() {
    }
}
