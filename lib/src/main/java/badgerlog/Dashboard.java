package badgerlog;

import badgerlog.entry.Entry;
import badgerlog.entry.configuration.Configuration;
import badgerlog.networktables.DashboardUtil;
import badgerlog.networktables.Updater;
import badgerlog.networktables.entries.SendableEntry;
import badgerlog.networktables.entries.publisher.Publisher;
import badgerlog.networktables.entries.publisher.PublisherFactory;
import badgerlog.networktables.entries.publisher.PublisherUpdater;
import badgerlog.networktables.entries.subscriber.Subscriber;
import badgerlog.networktables.entries.subscriber.SubscriberFactory;
import badgerlog.networktables.entries.subscriber.SubscriberUpdater;
import badgerlog.networktables.entries.subscriber.ValueSubscriber;
import badgerlog.networktables.mappings.Mapping;
import badgerlog.networktables.mappings.MappingType;
import badgerlog.networktables.mappings.Mappings;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.event.EventLoop;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.FieldInfo;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * This class is the base class for BadgerLog, providing methods for initialization, updating, and utility functions for NetworkTables.
 * <br>
 * <h2 style="font-size:13px;">Requirements for BadgerLog to function correctly</h2>
 * <ul>
 * <li> {@link #initialize(DashboardConfig)} must be called on robot initialization (Robot.robotInit) for BadgerLog to create the publishers and subscribers for NetworkTables.
 * <li> {@link #update()} must be called periodically (Robot.robotPeriodic) for values on NetworkTables to be updated
 * <li> Any values put to NetworkTables with {@link Entry} or {@link #putValue} must have an associated {@link Mapping} or an error will be thrown
 * </ul><br> <br>
 *
 * <h2 style="font-size:13px;">Additional Utilities BadgerLog has</h2>
 * <ul>
 * <li> Create a {@link Trigger} bound to a NetworkTables boolean for events
 * <li> Put and get values to NetworkTables from a method
 * </ul>
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
     * Initialize method used to register all the type mappings and find all fields annotated with {@link Entry}. This should be called on robot startup, usually Robot.robotInit
     *
     * @param config the configuration for BadgerLog to use
     */
    @SneakyThrows({InterruptedException.class, ExecutionException.class})
    public static void initialize(DashboardConfig config) {
        Dashboard.config = config;
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
                DriverStation.reportError("No entry found for field: " + field.getName(), true);
                continue;
            }
            var fieldConfig = DashboardUtil.createConfigurationFromField(field);
            String key;
            if (fieldConfig.getKey() == null || fieldConfig.getKey().isBlank())
                key = fieldInfo.getClassInfo().getSimpleName() + "/" + fieldInfo.getName();
            else key = fieldConfig.getKey();

            ntEntries.put(key, switch (entry.value()) {
                case Publisher ->
                        new PublisherUpdater<>(PublisherFactory.getPublisherFromValue(key, DashboardUtil.getFieldValue(field), fieldConfig), () -> DashboardUtil.getFieldValue(field));
                case Subscriber ->
                        new SubscriberUpdater<>(SubscriberFactory.getSubscriberFromValue(key, DashboardUtil.getFieldValue(field), fieldConfig), value -> DashboardUtil.setFieldValue(field, value));
                case Sendable -> new SendableEntry(key, DashboardUtil.getFieldValue(field));
            });
        }
        result.close();

        defaultTable.getEntry("Dashboard Startup").setBoolean(true);
        isInitialized = true;
    }

    /**
     * Method to update all values from BadgerLog in NetworkTables. This should be called in a periodic method, usually Robot.robotPeriodic
     */
    public static void update() {
        checkDashboardInitialized();
        ntEntries.forEach((Key, entry) -> entry.update());
    }

    /**
     * Method to get a {@link Trigger} bound to a NetworkTables boolean to all for a 'button' on NetworkTables
     *
     * @param key       the key for the subscriber
     * @param eventLoop the {@link EventLoop} for the Trigger to be bound to
     * @return the Trigger
     */
    public static Trigger getNetworkTablesButton(String key, EventLoop eventLoop) {
        checkDashboardInitialized();

        var subscriber = new ValueSubscriber<>(key, boolean.class, false, null);
        return new Trigger(eventLoop, subscriber::retrieveValue);
    }

    /**
     * Method to get a {@link Trigger} that auto resets after 0.25s
     *
     * @param key       the key for the subscriber
     * @param eventLoop the {@link EventLoop} for the Trigger to be bound to
     * @return the Trigger that auto resets the NetworkTables value after 0.25s
     * @see #getNetworkTablesButton
     */
    public static Trigger getAutoResettingButton(String key, EventLoop eventLoop) {
        checkDashboardInitialized();

        return getNetworkTablesButton(key, eventLoop)
                .onTrue(Commands.waitSeconds(0.25).andThen(new InstantCommand(() -> putValue(key, false)).ignoringDisable(true)));
    }

    /**
     * Method to put a generic value to NetworkTables at the specified key. The type must have a registered mapping or be of struct type, otherwise an error will be thrown
     *
     * @param key   the key for NetworkTables
     * @param value the value to be published
     * @param <T>   the type to be published
     * @see #putValue(String, Object, Configuration)
     */
    public static <T> void putValue(String key, T value) {
        putValue(key, value, new Configuration());
    }

    /**
     * Method to put a generic value to NetworkTables at the specified key. A configuration value for the mapping may be provided, but can be null. The type must have a registered mapping or be of struct type, otherwise an error will be thrown.
     *
     * @param key    the key for NetworkTables
     * @param value  the value to be published
     * @param config configuration options for {@link Mapping} types
     * @param <T>    the type to be published
     */
    @SuppressWarnings("unchecked")
    public static <T> void putValue(String key, T value, Configuration config) {
        checkDashboardInitialized();

        Publisher<T> publisher;
        if (!singleUsePublishers.containsKey(key)) {
            publisher = PublisherFactory.getPublisherFromValue(key, value, config);
            singleUsePublishers.put(key, publisher);
        } else {
            publisher = (Publisher<T>) singleUsePublishers.get(key);
        }

        publisher.publishValue(value);
    }

    /**
     * Method to get a generic value from one on NetworkTables at the specified key. The type must have a registered mapping or be of struct type, otherwise an error will be thrown.
     *
     * @param key          the key for NetworkTables
     * @param defaultValue the default value on NetworkTables
     * @param <T>          the type of be subscribed to
     * @return the value on NetworkTables
     * @see #getValue(String, Object, Configuration)
     */
    public static <T> T getValue(String key, T defaultValue) {
        return getValue(key, defaultValue, new Configuration());
    }

    /**
     * Method to get a generic value from one on NetworkTables at the specified key. A configuration value for the mapping may be provided, but can be null. The type must have a registered mapping or be of struct type, otherwise an error will be thrown.
     *
     * @param key          the key for NetworkTables
     * @param defaultValue the default value on NetworkTables
     * @param config       the config option
     * @param <T>          the type of be subscribed to
     * @return the value on NetworkTables
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValue(String key, T defaultValue, Configuration config) {
        checkDashboardInitialized();
        Subscriber<T> subscriber;
        if (!singleUseSubscribers.containsKey(key)) {
            subscriber = SubscriberFactory.getSubscriberFromValue(key, defaultValue, config);
            singleUseSubscribers.put(key, subscriber);
        } else {
            subscriber = (Subscriber<T>) singleUseSubscribers.get(key);
        }

        return subscriber.retrieveValue();
    }


    private static void checkDashboardInitialized() {
        if (!isInitialized)
            throw new IllegalStateException("Dashboard is not initialized. \n Call Dashboard.initialize() in Robot.robotInit");
    }
}
