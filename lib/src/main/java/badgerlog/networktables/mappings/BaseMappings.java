package badgerlog.networktables.mappings;

import badgerlog.entry.Configuration;
import edu.wpi.first.networktables.NetworkTableType;

import javax.annotation.Nonnull;

/**
 * Collection of {@link Mapping Mappings} for NetworkTables, includes only {@link Double}, {@link Boolean}, {@link Integer}, and {@link String} mappings
 */
public final class BaseMappings {

    @MappingType
    public static Mapping<Double, Double> doubleMapping = new Mapping<>(double.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(@Nonnull Double startValue, @Nonnull Configuration config) {
            return startValue;
        }

        @Override
        public Double toStart(@Nonnull Double ntValue, @Nonnull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<Double, Double> double1Mapping = new Mapping<>(Double.class, Double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(@Nonnull Double startValue, @Nonnull Configuration config) {
            return startValue;
        }

        @Override
        public Double toStart(@Nonnull Double ntValue, @Nonnull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<Float, Float> floatMapping = new Mapping<>(float.class, float.class, NetworkTableType.kFloat) {

        @Override
        public Float toNT(@Nonnull Float startValue, @Nonnull Configuration config) {
            return startValue;
        }

        @Override
        public Float toStart(@Nonnull Float ntValue, @Nonnull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<Float, Float> float1Mapping = new Mapping<>(Float.class, Float.class, NetworkTableType.kFloat) {

        @Override
        public Float toNT(@Nonnull Float startValue, @Nonnull Configuration config) {
            return startValue;
        }

        @Override
        public Float toStart(@Nonnull Float ntValue, @Nonnull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<Boolean, Boolean> booleanMapping = new Mapping<>(boolean.class, boolean.class, NetworkTableType.kBoolean) {

        @Override
        public Boolean toNT(@Nonnull Boolean startValue, @Nonnull Configuration config) {
            return startValue;
        }

        @Override
        public Boolean toStart(@Nonnull Boolean ntValue, @Nonnull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<Boolean, Boolean> boolean1Mapping = new Mapping<>(Boolean.class, Boolean.class, NetworkTableType.kBoolean) {

        @Override
        public Boolean toNT(@Nonnull Boolean startValue, @Nonnull Configuration config) {
            return startValue;
        }

        @Override
        public Boolean toStart(@Nonnull Boolean ntValue, @Nonnull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<Integer, Integer> integerMapping = new Mapping<>(int.class, int.class, NetworkTableType.kInteger) {

        @Override
        public Integer toNT(@Nonnull Integer startValue, @Nonnull Configuration config) {
            return startValue;
        }

        @Override
        public Integer toStart(@Nonnull Integer ntValue, @Nonnull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<Integer, Integer> integer1Mapping = new Mapping<>(Integer.class, Integer.class, NetworkTableType.kInteger) {

        @Override
        public Integer toNT(@Nonnull Integer startValue, @Nonnull Configuration config) {
            return startValue;
        }

        @Override
        public Integer toStart(@Nonnull Integer ntValue, @Nonnull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<String, String> stringMapping = new Mapping<>(String.class, String.class, NetworkTableType.kString) {

        @Override
        public String toNT(@Nonnull String startValue, @Nonnull Configuration config) {
            return startValue;
        }

        @Override
        public String toStart(@Nonnull String ntValue, @Nonnull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<double[], double[]> doubleArrayMapping = new Mapping<>(double[].class, double[].class, NetworkTableType.kDoubleArray) {

        @Override
        public double[] toNT(@Nonnull double[] startValue, @Nonnull Configuration config) {
            return startValue;
        }

        @Override
        public double[] toStart(@Nonnull double[] ntValue, @Nonnull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<boolean[], boolean[]> booleanArrayMapping = new Mapping<>(boolean[].class, boolean[].class, NetworkTableType.kBooleanArray) {

        @Override
        public boolean[] toNT(@Nonnull boolean[] startValue, @Nonnull Configuration config) {
            return startValue;
        }

        @Override
        public boolean[] toStart(@Nonnull boolean[] ntValue, @Nonnull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<String[], String[]> stringArrayMapping = new Mapping<>(String[].class, String[].class, NetworkTableType.kStringArray) {

        @Override
        public String[] toNT(@Nonnull String[] startValue, @Nonnull Configuration config) {
            return startValue;
        }

        @Override
        public String[] toStart(@Nonnull String[] ntValue, @Nonnull Configuration config) {
            return ntValue;
        }
    };

    @MappingType
    public static Mapping<float[], float[]> floatArrayMapping = new Mapping<>(float[].class, float[].class, NetworkTableType.kFloatArray) {

        @Override
        public float[] toNT(@Nonnull float[] startValue, @Nonnull Configuration config) {
            return startValue;
        }

        @Override
        public float[] toStart(@Nonnull float[] ntValue, @Nonnull Configuration config) {
            return ntValue;
        }
    };

    private BaseMappings() {
    }
}
