package badgerlog.networktables.mappings;

import badgerlog.entry.configuration.Configuration;
import edu.wpi.first.networktables.NetworkTableType;
import org.jetbrains.annotations.NotNull;

/**
 * Collection of {@link Mapping Mappings} for NetworkTables, includes only {@link Double}, {@link Boolean}, {@link Integer}, and {@link String} mappings
 */
//TODO array type mappings for all array types
public final class BaseMappings {

    /**
     * Mapping from {@link Double} to {@link Double} (primitive)
     */
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
    /**
     * Mapping from {@link Double} to {@link Double} (object)
     */
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
    /**
     * Mapping from {@link Boolean} to {@link Boolean} (primitive)
     */
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
    /**
     * Mapping from {@link Boolean} to {@link Boolean} (object)
     */
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
    /**
     * Mapping from {@link Integer} to {@link Double} (primitive)
     */
    @MappingType
    public static Mapping<Integer, Double> integerMapping = new Mapping<>(int.class, double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(@NotNull Integer startValue, @NotNull Configuration config) {
            return startValue.doubleValue();
        }

        @Override
        public Integer toStart(@NotNull Double ntValue, @NotNull Configuration config) {
            return ntValue.intValue();
        }
    };
    /**
     * Mapping from {@link Integer} to {@link Double} (object)
     */
    @MappingType
    public static Mapping<Integer, Double> integer1Mapping = new Mapping<>(Integer.class, Double.class, NetworkTableType.kDouble) {

        @Override
        public Double toNT(@NotNull Integer startValue, @NotNull Configuration config) {
            return startValue.doubleValue();
        }

        @Override
        public Integer toStart(@NotNull Double ntValue, @NotNull Configuration config) {
            return ntValue.intValue();
        }
    };
    /**
     * Mapping from {@link String} to {@link String}
     */
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

    private BaseMappings() {
    }
}
