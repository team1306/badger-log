package badgerlog;

import badgerlog.networktables.entries.publisher.Publisher;
import edu.wpi.first.util.struct.Struct;
import lombok.Getter;

/**
 * Configuration options for BadgerLog.
 * <br>
 * <li>baseTablekey: The base table for NetworkTables, will not be under any table.
 * <li>structOptions: The type of {@link Publisher} to use for a {@link Struct}.
 * <li>basePackages: The base package for ClassGraph to scan through.
 *
 * @see StructOptions
 */
@Getter
public final class DashboardConfig {

    /**
     * The default configuration for BadgerLog.
     * <br/>
     * <li>
     * Base Table Key: "badgerlog"
     * <li>
     * Struct Options: StructOptions.SUB_TABLE
     * <li>
     * Base Package: "frc.robot"
     */
    public static final DashboardConfig defaultConfig = new DashboardConfig();
    public static final DashboardConfig smartDashboardConfig = new DashboardConfig()
            .withStructOptions(StructOptions.STRUCT)
            .withBaseTableKey("SmartDashboard");

    private String baseTableKey = "badgerlog";
    private StructOptions structOptions = StructOptions.SUB_TABLE;
    private String[] basePackages = {"frc.robot"};

    private DashboardConfig() {
    }

    /**
     * Change the baseTableKey to the one provided
     *
     * @param defaultTableKey the base table key
     * @return the instance it was called with for method chaining
     * @see DashboardConfig
     */
    public DashboardConfig withBaseTableKey(String defaultTableKey) {
        this.baseTableKey = defaultTableKey;
        return this;
    }

    /**
     * Change the structOptions to the one provided
     *
     * @param structOptions the options for publishing structs
     * @return the instance it was called with for method chaining
     * @see StructOptions
     */
    public DashboardConfig withStructOptions(StructOptions structOptions) {
        this.structOptions = structOptions;
        return this;
    }

    /**
     * Change the basePackages to the ones provided
     *
     * @param defaultPackages the default packages for ClassGraph to scan through
     * @return the instance it was called with for method chaining
     * @see DashboardConfig
     */
    public DashboardConfig withBasePackages(String... defaultPackages) {
        this.basePackages = defaultPackages;
        return this;
    }

    /**
     * Options for changing how {@link Struct} are published to NetworkTables
     */
    public enum StructOptions {
        /**
         * Publish the struct as is and registering the schema with NetworkTables
         */
        STRUCT,
        /**
         * Publish the struct as a collection of subtables
         */
        SUB_TABLE,
        /**
         * Publish the struct as a value mapped to a NetworkTableType
         */
        MAPPING
    }
}
