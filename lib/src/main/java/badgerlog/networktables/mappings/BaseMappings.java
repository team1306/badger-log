package badgerlog.networktables.mappings;

import badgerlog.entry.Configuration;
import edu.wpi.first.networktables.NetworkTableType;

import javax.annotation.Nonnull;

/**
 * Provides base {@link Mapping} implementations for standard data types to NetworkTables entries.
 * Includes mappings for primitives (e.g., double, boolean), their boxed equivalents (e.g., Double, Boolean),
 * and arrays of these types. Each mapping performs direct conversion.
 * <p>
 * This class cannot be instantiated; all mappings are static and predefined.
 */
public final class BaseMappings {

    /**
     * Mapping for primitive double values to NetworkTables Double values.
     */
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

    /**
     * Mapping for boxed Double values to NetworkTables Double values.
     */
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

    /**
     * Mapping for primitive float values to NetworkTables Float values.
     */
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

    /**
     * Mapping for boxed Float values to NetworkTables Float values.
     */
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

    /**
     * Mapping for primitive boolean values to NetworkTables Boolean values.
     */
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

    /**
     * Mapping for boxed Boolean values to NetworkTables Boolean values.
     */
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

    /**
     * Mapping for primitive integer values to NetworkTables Integer values.
     */
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

    /**
     * Mapping for boxed Integer values to NetworkTables Integer values.
     */
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

    /**
     * Mapping for String values to NetworkTables String values.
     */
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

    /**
     * Mapping for primitive double arrays to NetworkTables double array values.
     */
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

    /**
     * Mapping for primitive boolean arrays to NetworkTables boolean array values.
     */
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

    /**
     * Mapping for String arrays to NetworkTables String array values.
     */
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

    /**
     * Mapping for primitive float arrays to NetworkTables float arrays values.
     */
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
