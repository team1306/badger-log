package badgerlog;

import badgerlog.annotations.Entry;
import badgerlog.annotations.configuration.Configuration;
import badgerlog.conversion.Mapping;
import badgerlog.networktables.*;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.event.EventLoop;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The {@code Dashboard} class serves as the main component of BadgerLog, providing a utility
 * for interacting with NetworkTables. It is intended as a modern replacement
 * for {@code SmartDashboard} with enhanced functionality.
 * It facilitates automatic discovery and synchronization of
 * fields annotated with {@link Entry}, manages publishers and subscribers, and offers utilities for
 * NetworkTables events. This class is designed for static use and must be initialized
 * before any operations.
 *
 * <h2>Key Functionalities</h2>
 * <ul>
 *   <li><b>Automatic Field Registration:</b> Discovers fields annotated with {@link Entry} and maps them
 *       to NetworkTables entries as publishers, subscribers, or Sendable objects.</li>
 *   <li><b>Organization of Entries:</b> Organizes fields annotated with {@link Entry} into subtables according to the containing class's name.</li>
 *   <li><b>Type Mappings:</b> Uses {@link Mapping} configurations to map any type to a valid NetworkTable type.</li>
 *   <li><b>Event Triggers:</b> Creates {@link Trigger} instances bound to NetworkTables boolean entries
 *       for event logic.</li>
 *   <li><b>Utilities:</b> Provides methods like {@link #putValue} and {@link #getValue} that use {@code Mapping} configurations.</li>
 * </ul>
 *
 * <h2>Initialization and Lifecycle</h2>
 *
 * <h2>Usage Requirements</h2>
 * <ul>
 *   <li>All types used with {@link #putValue} or {@link #getValue} must have a registered {@link Mapping}.</li>
 *   <li>Fields annotated with {@link Entry} must be non-final, static, and initialized for auto-registration.</li>
 *   <li>NetworkTables keys must be unique to avoid type conflicts.</li>
 * </ul>
 *
 * @see Entry
 * @see Mapping
 * @see Trigger
 * @see DashboardConfig
 */
public final class Dashboard {

    private static final Set<Updater> ntEntries = new HashSet<>();
    private static final HashMap<String, NTEntry<?>> activeEntries = new HashMap<>();
    /**
     * The config used by BadgerLog
     */
    public static DashboardConfig config = DashboardConfig.defaultConfig;
    /**
     * The base table used by BadgerLog for publishing and subscribing to
     */
    public static NetworkTable defaultTable = NetworkTableInstance.getDefault().getTable("BadgerLog");

    private Dashboard() {
    }

    /**
     * Initializes BadgerLog.
     * This method performs one-time setup including:
     * <ul>
     *   <li>Configuration of BadgerLog base table using provided {@link DashboardConfig}</li>
     *   <li>Classpath scanning for fields annotated with {@link Entry} and </li>
     *   <li>Automatic creation of publishers/subscribers for discovered fields</li>
     *   <li>Creation of type mappings for NetworkTables data conversions</li>
     * </ul>
     * <p>
     * Must be called exactly once during robot initialization (typically in {@code Robot.robotInit}).
     * <br /> <br />
     * Requires all fields annotated with {@code Entry} or {@code MappingType} to be initialized, non-final and static. Access level does not matter. Package scanning
     * configuration in {@code DashboardConfig} must include all packages with dashboard-related fields.
     *
     * @param config The configuration object
     * @throws IllegalStateException If annotated fields are improperly configured (uninitialized, non-static, or final)
     * @see Entry
     */
    public static void initialize(DashboardConfig config) {
        Dashboard.config = config;
    }

    public static <T extends Enum<T>> Optional<SendableChooser<Enum<T>>> createSelectorFromEnum(String key, Class<T> tEnum, Consumer<Enum<T>> onValueChange) {
        if (validateEnum(tEnum)) {
            System.out.println(key + " was trying to create an enum selector, but failed. SKIPPING");
            return Optional.empty();
        }
        return createSelectorFromEnum(key, tEnum, tEnum.getEnumConstants()[0], onValueChange);
    }

    public static <T extends Enum<T>> Optional<SendableChooser<Enum<T>>> createSelectorFromEnum(String key, Class<T> tEnum, Enum<T> defaultValue, Consumer<Enum<T>> onValueChange) {
        if (validateEnum(tEnum)) {
            System.out.println(key + " was trying to create an enum selector, but failed. SKIPPING");
            return Optional.empty();
        }
        SendableChooser<Enum<T>> chooser = new SendableChooser<>();
        chooser.setDefaultOption(defaultValue.toString(), defaultValue);
        for (Enum<T> value : tEnum.getEnumConstants()) {
            if (value == defaultValue) continue;
            chooser.addOption(value.toString(), value);
        }
        chooser.onChange(onValueChange);
        onValueChange.accept(defaultValue);
        Dashboard.putSendable(key, chooser);
        return Optional.of(chooser);
    }

    private static <T extends Enum<T>> boolean validateEnum(Class<T> tEnum) {
        if (!tEnum.isEnum()) {
            System.out.println("Tried to create an enum selector, but the class was not an enum");
            return true;
        }
        if (tEnum.getEnumConstants().length == 0) {
            System.out.println("Tried to create an enum selector, but the enum had no values");
            return true;
        }
        return false;
    }

    public static void addNetworkTableEntry(NTEntry<?> entry) {
        activeEntries.put(entry.getKey(), entry);
    }

    public static void addUpdatingNetworkTableEntry(Updater entry) {
        ntEntries.add(entry);
    }

    /**
     * Updates all registered NetworkTables entries with current values. This method must be called
     * periodically (typically in {@code Robot.robotPeriodic}) to update fields and NetworkTable values.
     *
     */
    public static void update() {
        ntEntries.forEach(Updater::update);
    }

    /**
     * Creates a {@link Trigger} bound to a boolean NetworkTables entry.
     *
     * @param key       The NetworkTables entry key
     * @param eventLoop The {@link EventLoop} to associate with the trigger for event polling
     * @return A trigger bound to the boolean NetworkTables entry's state
     * @see Trigger
     * @see EventLoop
     */
    public static Trigger getNetworkTablesButton(String key, EventLoop eventLoop) {
        var subscriber = new ValueEntry<>(key, boolean.class, false, new Configuration());
        return new Trigger(eventLoop, subscriber::retrieveValue);
    }

    /**
     * Creates a {@link Trigger} that automatically resets its associated boolean NetworkTables entry
     * to {@code false} 0.25 seconds after activation.
     *
     * @param key       The NetworkTables entry key
     * @param eventLoop The {@link EventLoop} to associate with the trigger for event polling
     * @return A trigger that automatically resets the NetworkTables value after activation
     * @see #getNetworkTablesButton(String, EventLoop)
     */
    public static Trigger getAutoResettingButton(String key, EventLoop eventLoop) {
        return getNetworkTablesButton(key, eventLoop)
                .onTrue(Commands.waitSeconds(0.25).andThen(new InstantCommand(() -> putValue(key, false)).ignoringDisable(true)));
    }

    /**
     * Publish a value to NetworkTables using the default configuration.
     *
     * @param key   The NetworkTables entry key
     * @param value The value to publish.
     * @param <T>   The data type to publish
     * @see #putValue(String, Object, Configuration)
     * @see Mapping
     * @see Configuration
     */
    public static <T> void putValue(String key, T value) {
        putValue(key, value, new Configuration());
    }

    /**
     * Publish a value to NetworkTables with custom configuration options.
     *
     * @param key    The NetworkTables entry key, will ignore config one
     * @param value  The value to publish.
     * @param config Configuration parameters
     * @param <T>    The data type to publish
     * @see #putValue(String, Object)
     */
    public static <T> void putValue(String key, T value, Configuration config) {
        NTEntry<T> entry = createEntryIfNotPresent(key, value, config);

        entry.publishValue(value);
    }

    /**
     * Retrieves a value from NetworkTables using the default configuration.
     *
     * @param key          The NetworkTables entry key, will ignore config one
     * @param defaultValue Initial value if the entry is not present
     * @param <T>          The data type to convert to after retrieval from NetworkTables
     * @return The current NetworkTables value
     * @see #getValue(String, Object, Configuration)
     */
    public static <T> T getValue(String key, T defaultValue) {
        return getValue(key, defaultValue, new Configuration());
    }

    /**
     * Retrieves a value from NetworkTables with custom configuration options.
     *
     * @param key          The NetworkTables entry key to read
     * @param defaultValue Initial value if the entry is not present
     * @param config       Configuration parameters
     * @param <T>          The data type to convert to after retrieval from NetworkTables
     * @return The current NetworkTables value
     * @see #getValue(String, Object)
     */
    public static <T> T getValue(String key, T defaultValue, Configuration config) {
        NTEntry<T> entry = createEntryIfNotPresent(key, defaultValue, config);

        return entry.retrieveValue();
    }

    @SuppressWarnings("unchecked")
    private static <T> NTEntry<T> createEntryIfNotPresent(String key, T defaultValue, Configuration config) {
        NTEntry<T> entry;
        if (!activeEntries.containsKey(key)) {
            entry = EntryFactory.createNetworkTableEntryFromValue(key, defaultValue, config);
            addNetworkTableEntry(entry);
        } else {
            entry = (NTEntry<T>) activeEntries.get(key);
        }
        return entry;
    }

    /**
     * Manually registers a {@link Sendable} object with NetworkTables.
     *
     * <p><strong>Recommended Approach:</strong> Prefer using {@code @Entry(EntryType.Sendable)}
     * on Sendable fields for automatic discovery and lifecycle management.</p>
     * <p>
     * Subsequent calls with same key are ignored.
     *
     * @param key      The NetworkTables entry key
     * @param sendable The Sendable object to publish
     * @see Entry
     */
    public static void putSendable(String key, Sendable sendable) {
        ntEntries.add(new SendableEntry(key, sendable));
    }
}
