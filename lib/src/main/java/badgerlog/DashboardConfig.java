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
            .withStructOptions(StructOptions.STRUCT)
            .withBaseTableKey("SmartDashboard");

    /**
     * The root key for the NetworkTables table where entries are published.
     *
     * @return String - Current base table key value
     */
    private String baseTableKey = "Badgerlog";

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
     * Sets the root NetworkTables table key for entries.
     *
     * @param defaultTableKey New base table key (e.g., "SmartDashboard")
     * @return This config instance for chaining
     */
    public DashboardConfig withBaseTableKey(String defaultTableKey) {
        this.baseTableKey = defaultTableKey;
        return this;
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
