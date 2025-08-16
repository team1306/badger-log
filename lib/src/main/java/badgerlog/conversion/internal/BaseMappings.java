package badgerlog.conversion.internal;

import badgerlog.annotations.configuration.Configuration;
import badgerlog.conversion.Mapping;
import badgerlog.conversion.Mappings;
import edu.wpi.first.networktables.NetworkTableType;

/**
 * Provides base {@link Mapping} implementations for standard data types to NetworkTables entries.
 * Includes mappings for primitives (e.g., double, boolean), their boxed equivalents (e.g., Double, Boolean),
 * and arrays of these types. Each mapping performs direct conversion.
 * <p>
 * This class cannot be instantiated; all mappings are static and predefined.
 */
public final class BaseMappings {

    private static final Mapping<Double, Double> doubleMapping = new Mapping<>(double.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(Double startValue, Configuration config) {
            return startValue;
        }

        @Override
        public Double toStart(Double ntValue, Configuration config) {
            return ntValue;
        }
    };
    private static final Mapping<Double, Double> double1Mapping = new Mapping<>(Double.class, Double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(Double startValue, Configuration config) {
            return startValue;
        }

        @Override
        public Double toStart(Double ntValue, Configuration config) {
            return ntValue;
        }
    };

    private static final Mapping<Float, Float> floatMapping = new Mapping<>(float.class, float.class, NetworkTableType.kFloat) {

        @Override
        public Float toNT(Float startValue, Configuration config) {
            return startValue;
        }

        @Override
        public Float toStart(Float ntValue, Configuration config) {
            return ntValue;
        }
    };
    private static final Mapping<Float, Float> float1Mapping = new Mapping<>(Float.class, Float.class, NetworkTableType.kFloat) {

        @Override
        public Float toNT(Float startValue, Configuration config) {
            return startValue;
        }

        @Override
        public Float toStart(Float ntValue, Configuration config) {
            return ntValue;
        }
    };

    private static final Mapping<Boolean, Boolean> booleanMapping = new Mapping<>(boolean.class, boolean.class, NetworkTableType.kBoolean) {

        @Override
        public Boolean toNT(Boolean startValue, Configuration config) {
            return startValue;
        }

        @Override
        public Boolean toStart(Boolean ntValue, Configuration config) {
            return ntValue;
        }
    };
    private static final Mapping<Boolean, Boolean> boolean1Mapping = new Mapping<>(Boolean.class, Boolean.class, NetworkTableType.kBoolean) {

        @Override
        public Boolean toNT(Boolean startValue, Configuration config) {
            return startValue;
        }

        @Override
        public Boolean toStart(Boolean ntValue, Configuration config) {
            return ntValue;
        }
    };

    private static final Mapping<Integer, Integer> integerMapping = new Mapping<>(int.class, int.class, NetworkTableType.kInteger) {

        @Override
        public Integer toNT(Integer startValue, Configuration config) {
            return startValue;
        }

        @Override
        public Integer toStart(Integer ntValue, Configuration config) {
            return ntValue;
        }
    };
    private static final Mapping<Integer, Integer> integer1Mapping = new Mapping<>(Integer.class, Integer.class, NetworkTableType.kInteger) {

        @Override
        public Integer toNT(Integer startValue, Configuration config) {
            return startValue;
        }

        @Override
        public Integer toStart(Integer ntValue, Configuration config) {
            return ntValue;
        }
    };

    private static final Mapping<String, String> stringMapping = new Mapping<>(String.class, String.class, NetworkTableType.kString) {

        @Override
        public String toNT(String startValue, Configuration config) {
            return startValue;
        }

        @Override
        public String toStart(String ntValue, Configuration config) {
            return ntValue;
        }
    };

    private static final Mapping<double[], double[]> doubleArrayMapping = new Mapping<>(double[].class, double[].class, NetworkTableType.kDoubleArray) {

        @Override
        public double[] toNT(double[] startValue, Configuration config) {
            return startValue;
        }

        @Override
        public double[] toStart(double[] ntValue, Configuration config) {
            return ntValue;
        }
    };

    private static final Mapping<boolean[], boolean[]> booleanArrayMapping = new Mapping<>(boolean[].class, boolean[].class, NetworkTableType.kBooleanArray) {

        @Override
        public boolean[] toNT(boolean[] startValue, Configuration config) {
            return startValue;
        }

        @Override
        public boolean[] toStart(boolean[] ntValue, Configuration config) {
            return ntValue;
        }
    };

    private static final Mapping<String[], String[]> stringArrayMapping = new Mapping<>(String[].class, String[].class, NetworkTableType.kStringArray) {

        @Override
        public String[] toNT(String[] startValue, Configuration config) {
            return startValue;
        }

        @Override
        public String[] toStart(String[] ntValue, Configuration config) {
            return ntValue;
        }
    };

    private static final Mapping<float[], float[]> floatArrayMapping = new Mapping<>(float[].class, float[].class, NetworkTableType.kFloatArray) {

        @Override
        public float[] toNT(float[] startValue, Configuration config) {
            return startValue;
        }

        @Override
        public float[] toStart(float[] ntValue, Configuration config) {
            return ntValue;
        }
    };

    private BaseMappings() {
    }

    public static void registerAllMappings() {
        Mappings.registerAllMappings(
                doubleMapping,
                double1Mapping,
                floatMapping,
                float1Mapping,
                stringMapping,
                booleanMapping,
                boolean1Mapping,
                booleanArrayMapping,
                stringArrayMapping,
                integerMapping,
                integer1Mapping,
                doubleArrayMapping,
                floatArrayMapping
        );
    }
}
