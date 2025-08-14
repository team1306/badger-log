package badgerlog;

import badgerlog.entry.Configuration;
import badgerlog.entry.Entry;
import badgerlog.networktables.entries.*;
import badgerlog.networktables.mappings.Mapping;
import badgerlog.networktables.mappings.MappingType;
import badgerlog.networktables.mappings.Mappings;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.event.EventLoop;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.FieldInfo;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

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
 * <ul>
 *   <li>{@link #initialize(DashboardConfig)} must be called during robot initialization (usually in
 *       {@code Robot.robotInit}) to scan for annotated fields and set up NetworkTables entries.</li>
 *   <li>{@link #update()} must be invoked periodically (usually in {@code Robot.robotPeriodic}) to refresh
 *       NetworkTables values.</li>
 * </ul>
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
 * @see Publisher
 * @see Subscriber
 * @see Trigger
 * @see DashboardConfig
 */
public final class Dashboard {

    private static final HashMap<String, Updater> ntEntries = new HashMap<>();
    private static final HashMap<String, Publisher<?>> singleUsePublishers = new HashMap<>();
    private static final HashMap<String, Subscriber<?>> singleUseSubscribers = new HashMap<>();
    /**
     * The config used by BadgerLog
     */
    public static DashboardConfig config = DashboardConfig.defaultConfig;
    /**
     * The base table used by BadgerLog for publishing and subscribing to
     */
    public static NetworkTable defaultTable = NetworkTableInstance.getDefault().getTable(config.getBaseTableKey());
    private static boolean isInitialized = false;

    private Dashboard() {
    }

    /**
     * Initializes BadgerLog.
     * This method performs one-time setup including:
     * <ul>
     *   <li>Configuration of BadgerLog base table using provided {@link DashboardConfig}</li>
     *   <li>Classpath scanning for fields annotated with {@link Entry} and {@link MappingType}</li>
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
     * @see MappingType
     */
    @SneakyThrows({InterruptedException.class, ExecutionException.class})
    public static void initialize(@Nonnull DashboardConfig config) {
        System.out.println("Before config set");
        Dashboard.config = config;
        System.out.println("After config set");
        defaultTable = NetworkTableInstance.getDefault().getTable(config.getBaseTableKey());
        if (isInitialized) return;

        defaultTable.getEntry("Dashboard Startup").setBoolean(false);

        var classGraph = new ClassGraph().acceptPackages(config.getBasePackages()).acceptPackages("badgerlog").enableAllInfo().ignoreFieldVisibility();

        var resultAsync = classGraph.scanAsync(Executors.newCachedThreadPool(), 10);
        var result = resultAsync.get();

        Mappings.mappings.addAll(
                result.getClassesWithFieldAnnotation(MappingType.class).stream()
                        .flatMap(classInfo -> classInfo.getFieldInfo().stream()
                                .filter(fieldInfo -> fieldInfo.hasAnnotation(MappingType.class))
                                .map(DashboardUtil::checkFieldValidity)
                                .map(field -> (Mapping<?, ?>) DashboardUtil.getFieldValue(field))
                        ).toList()
        );

        var fieldInfoCollection = result.getClassesWithFieldAnnotation(Entry.class).stream()
                .flatMap(classInfo -> classInfo.getFieldInfo().stream()
                        .filter(fieldInfo -> fieldInfo.hasAnnotation(Entry.class))
                ).toList();


        for (FieldInfo fieldInfo : fieldInfoCollection) {
            var field = DashboardUtil.checkFieldValidity(fieldInfo);

            var entry = field.getAnnotation(Entry.class);
            if (entry == null) {
                DriverStation.reportError("No entry annotation found for field: " + field.getName(), true);
                continue;
            }
            var fieldConfig = DashboardUtil.createConfigurationFromField(field);
            String key;
            if (fieldConfig.getKey() == null || fieldConfig.getKey().isBlank())
                key = fieldInfo.getClassInfo().getSimpleName() + "/" + fieldInfo.getName();
            else key = fieldConfig.getKey();

            ntEntries.put(key, switch (entry.value()) {
                case Publisher ->
                        new PublisherUpdater<>(EntryFactory.createPublisherFromValue(key, DashboardUtil.getFieldValue(field), fieldConfig), () -> DashboardUtil.getFieldValue(field));
                case Subscriber ->
                        new SubscriberUpdater<>(EntryFactory.createSubscriberFromValue(key, DashboardUtil.getFieldValue(field), fieldConfig), value -> DashboardUtil.setFieldValue(field, value));
                case Sendable -> new SendableEntry(key, (Sendable) DashboardUtil.getFieldValue(field));
            });
        }
        result.close();

        defaultTable.getEntry("Dashboard Startup").setBoolean(true);
        isInitialized = true;
    }

    /**
     * Updates all registered NetworkTables entries with current values. This method must be called
     * periodically (typically in {@code Robot.robotPeriodic}) to update fields and NetworkTable values.
     *
     * @throws IllegalStateException if called before {@link #initialize(DashboardConfig)}
     */
    public static void update() {
        checkDashboardInitialized();
        ntEntries.forEach((Key, entry) -> entry.update());
    }

    /**
     * Creates a {@link Trigger} bound to a boolean NetworkTables entry.
     *
     * @param key       The NetworkTables entry key
     * @param eventLoop The {@link EventLoop} to associate with the trigger for event polling
     * @return A trigger bound to the boolean NetworkTables entry's state
     * @throws IllegalStateException if called before {@link #initialize(DashboardConfig)}
     * @see Trigger
     * @see EventLoop
     */
    public static Trigger getNetworkTablesButton(@Nonnull String key, @Nonnull EventLoop eventLoop) {
        checkDashboardInitialized();

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
     * @throws IllegalStateException if called before {@link #initialize(DashboardConfig)}
     * @see #getNetworkTablesButton(String, EventLoop)
     */
    public static Trigger getAutoResettingButton(@Nonnull String key, @Nonnull EventLoop eventLoop) {
        checkDashboardInitialized();

        return getNetworkTablesButton(key, eventLoop)
                .onTrue(Commands.waitSeconds(0.25).andThen(new InstantCommand(() -> putValue(key, false)).ignoringDisable(true)));
    }

    /**
     * Publish a value to NetworkTables using the default configuration.
     *
     * @param key   The NetworkTables entry key
     * @param value The value to publish.
     * @param <T>   The data type to publish
     * @throws IllegalStateException if called before {@link #initialize(DashboardConfig)}
     * @see #putValue(String, Object, Configuration)
     * @see Mapping
     * @see Configuration
     */
    public static <T> void putValue(@Nonnull String key, @Nonnull T value) {
        putValue(key, value, new Configuration());
    }

    /**
     * Publish a value to NetworkTables with custom configuration options.
     *
     * @param key    The NetworkTables entry key, will ignore config one
     * @param value  The value to publish.
     * @param config Configuration parameters
     * @param <T>    The data type to publish
     * @throws IllegalStateException if called before {@link #initialize(DashboardConfig)}
     * @see #putValue(String, Object)
     */
    @SuppressWarnings("unchecked")
    public static <T> void putValue(@Nonnull String key, @Nonnull T value, @Nonnull Configuration config) {
        checkDashboardInitialized();

        Publisher<T> publisher;
        if (!singleUsePublishers.containsKey(key)) {
            publisher = EntryFactory.createPublisherFromValue(key, value, config);
            singleUsePublishers.put(key, publisher);
        } else {
            publisher = (Publisher<T>) singleUsePublishers.get(key);
        }

        publisher.publishValue(value);
    }

    /**
     * Retrieves a value from NetworkTables using the default configuration.
     *
     * @param key          The NetworkTables entry key, will ignore config one
     * @param defaultValue Initial value if the entry is not present
     * @param <T>          The data type to convert to after retrieval from NetworkTables
     * @return The current NetworkTables value
     * @throws IllegalStateException If {@link #initialize(DashboardConfig)} hasn't been called
     * @see #getValue(String, Object, Configuration)
     */
    public static <T> T getValue(@Nonnull String key, @Nonnull T defaultValue) {
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
     * @throws IllegalStateException If {@link #initialize(DashboardConfig)} hasn't been called
     * @see #getValue(String, Object)
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(@Nonnull String key, @Nonnull T defaultValue, @Nonnull Configuration config) {
        checkDashboardInitialized();
        Subscriber<T> subscriber;
        if (!singleUseSubscribers.containsKey(key)) {
            subscriber = EntryFactory.createSubscriberFromValue(key, defaultValue, config);
            singleUseSubscribers.put(key, subscriber);
        } else {
            subscriber = (Subscriber<T>) singleUseSubscribers.get(key);
        }

        return subscriber.retrieveValue();
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
     * @throws IllegalStateException If {@link #initialize(DashboardConfig)} hasn't been called
     * @see Entry
     */
    public static void putSendable(@Nonnull String key, @Nonnull Sendable sendable) {
        if (ntEntries.containsKey(key)) return;
        ntEntries.put(key, new SendableEntry(key, sendable));
    }

    private static void checkDashboardInitialized() {
        if (!isInitialized)
            throw new IllegalStateException("Dashboard is not initialized. \n Call Dashboard.initialize() in Robot.robotInit");
    }
}
