package badgerlog;

import badgerlog.annotations.StructType;
import lombok.Getter;

/**
 * Default configuration to use when no field specific configuration is present.
 */
@SuppressWarnings("unused")
@Getter
public final class DashboardConfig {

    public static final DashboardConfig defaultConfig = new DashboardConfig();

    /**
     * {@return the default struct publishing options}
     */
    private StructType structType = StructType.SUB_TABLE;

    private DashboardConfig() {
    }

    /**
     * {@return the configuration object for method chaining}
     *
     * @param structType the struct options to use for publishing
     */
    public DashboardConfig withStructType(StructType structType) {
        this.structType = structType;
        return this;
    }
}
