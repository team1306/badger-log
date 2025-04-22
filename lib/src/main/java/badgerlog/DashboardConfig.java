package badgerlog;

import badgerlog.networktables.entries.publisher.Publisher;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import lombok.Getter;

import javax.annotation.Nonnull;

/**
 * Configuration options for BadgerLog.
 * <br>
 * <ul>
 * <li>baseTablekey: The base table for NetworkTables, will not be under any table.
 * <li>structOptions: The type of {@link Publisher} to use for a {@link Struct}.
 * <li>basePackages: The base package for ClassGraph to scan through.
 * </ul>
 *
 * @see StructOptions
 */
@Getter
public final class DashboardConfig {

    /**
     * The default configuration for BadgerLog.
     * <ul>
     * <li> Base Table Key: "BadgerLog"
     * <li> Struct Options: StructOptions.SUB_TABLE
     * <li> Base Package: "frc.robot"
     * </ul>
     */
    public static final DashboardConfig defaultConfig = new DashboardConfig();

    /**
     * The configuration for easy use with other {@link SmartDashboard}
     * <ul>
     * <li> Base Table Key: "SmartDashboard"
     * <li> Struct Options: StructOptions.STRUCT
     * <li> Base Package: "frc.robot"
     * </ul>
     */
    public static final DashboardConfig smartDashboardConfig = new DashboardConfig()
            .withStructOptions(StructOptions.STRUCT)
            .withBaseTableKey("SmartDashboard");

    /**
     * Get the base key for BadgerLog on NetworkTables
     *
     * @return the key for the table on NetworkTables
     */
    private String baseTableKey = "BadgerLog";

    /**
     * Get the options to publish {@link Struct} to NetworkTables
     *
     * @return the StructOptions to use
     * @see StructOptions
     */
    private StructOptions structOptions = StructOptions.SUB_TABLE;

    /**
     * Get the list of packages to scan for on initialization
     *
     * @return an array of packages to scan
     */
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
    public DashboardConfig withBaseTableKey(@Nonnull String defaultTableKey) {
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
    public DashboardConfig withStructOptions(@Nonnull StructOptions structOptions) {
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


}
