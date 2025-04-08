package badgerlog;

import badgerlog.entry.Config;
import badgerlog.entry.Entry;
import badgerlog.networktables.DashboardUtil;
import badgerlog.networktables.Updater;
import badgerlog.networktables.entries.SendableEntry;
import badgerlog.networktables.entries.publisher.Publisher;
import badgerlog.networktables.entries.publisher.PublisherFactory;
import badgerlog.networktables.entries.publisher.PublisherUpdater;
import badgerlog.networktables.entries.subscriber.SubscriberFactory;
import badgerlog.networktables.entries.subscriber.SubscriberUpdater;
import badgerlog.networktables.entries.subscriber.ValueSubscriber;
import badgerlog.networktables.mappings.Mapping;
import badgerlog.networktables.mappings.MappingType;
import badgerlog.networktables.mappings.Mappings;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
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
 * 
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
 * <li> Put values to NetworkTables at arbitrary times without an Entry annotation and whatever value wanted
 * </ul>
 */
public final class Dashboard {

    private static final HashMap<String, Updater> ntEntries = new HashMap<>();
    private static final HashMap<String, Publisher<?>> singleUsePublishers = new HashMap<>();
    private static DashboardConfig config = DashboardConfig.defaultConfig;
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
            var key = entry.key();
            if (key.isEmpty()) key = fieldInfo.getClassInfo().getSimpleName() + "/" + fieldInfo.getName();

            String fieldConfig = null;
            if (fieldInfo.hasAnnotation(Config.class)) fieldConfig = field.getAnnotation(Config.class).value();

            ntEntries.put(key, switch (entry.type()) {
                case Publisher ->
                        new PublisherUpdater<>(PublisherFactory.getPublisherFromValue(config, key, DashboardUtil.getFieldValue(field), fieldConfig), () -> DashboardUtil.getFieldValue(field));
                case Subscriber ->
                        new SubscriberUpdater<>(SubscriberFactory.getSubscriberFromValue(config, key, DashboardUtil.getFieldValue(field), fieldConfig), value -> DashboardUtil.setFieldValue(field, value));
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
     * @see #putValue(String, Object, String)
     */
    public static <T> void putValue(String key, T value) {
        putValue(key, value, null);
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
    public static <T> void putValue(String key, T value, String config) {
        checkDashboardInitialized();

        Publisher<T> publisher;
        if (!singleUsePublishers.containsKey(key)) {
            publisher = (Publisher<T>) PublisherFactory.getPublisherFromValue(Dashboard.config, key, value, config);
            singleUsePublishers.put(key, publisher);
        } else {
            publisher = (Publisher<T>) singleUsePublishers.get(key);
        }

        publisher.publishValue(value);
    }


    private static void checkDashboardInitialized() {
        if (!isInitialized)
            throw new IllegalStateException("Dashboard is not initialized. \n Call Dashboard.initialize() in Robot.robotInit");
    }
}
