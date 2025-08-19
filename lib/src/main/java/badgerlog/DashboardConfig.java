package badgerlog;

import badgerlog.annotations.StructOptions;
import lombok.Getter;

/**
 * Configuration for customizing BadgerLog's NetworkTables integration and package scanning.
 */
@Getter
public final class DashboardConfig {

    /**
     * Default configuration with base table "BadgerLog", SUB_TABLE struct handling, and "frc.robot" package scanning.
     */
    public static final DashboardConfig defaultConfig = new DashboardConfig();

    /**
     * Preconfigured for similar configuration to SmartDashboard methods: base table "SmartDashboard", STRUCT type handling.
     */
    public static final DashboardConfig smartDashboardConfig = new DashboardConfig()
            .withStructOptions(StructOptions.STRUCT);
    /**
     * Defines how struct data types are published to NetworkTables.
     *
     * @return StructOptions - Current struct serialization strategy
     */
    private StructOptions structOptions = StructOptions.SUB_TABLE;

    /**
     * Base Java packages to scan for annotated fields during initialization.
     *
     * @return String[] - Array of packages being scanned
     */
    private String[] basePackages = {"frc.robot"};

    private DashboardConfig() {
    }

    /**
     * Defines how struct data types are published to NetworkTables.
     *
     * @param structOptions Publishing strategy (MAPPING, SUB_TABLE, or STRUCT)
     * @return This config instance for chaining
     */
    public DashboardConfig withStructOptions(StructOptions structOptions) {
        this.structOptions = structOptions;
        return this;
    }

    /**
     * Specifies packages to scan for annotated fields during initialization.
     *
     * @param defaultPackages Package names to scan (e.g., "frc.robot")
     * @return This config instance for chaining
     */
    public DashboardConfig withBasePackages(String... defaultPackages) {
        this.basePackages = defaultPackages;
        return this;
    }


}
