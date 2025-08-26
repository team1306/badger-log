package badgerlog;

import badgerlog.annotations.StructOptions;
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
    private StructOptions structOptions = StructOptions.SUB_TABLE;

    private DashboardConfig() {
    }

    /**
     * {@return the configuration object for method chaining}
     *
     * @param structOptions the struct options to use for publishing
     */
    public DashboardConfig withStructOptions(StructOptions structOptions) {
        this.structOptions = structOptions;
        return this;
    }
}
