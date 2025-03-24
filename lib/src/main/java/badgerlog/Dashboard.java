package badgerlog;

import badgerlog.entry.Config;
import badgerlog.entry.Entry;
import badgerlog.networktables.DashboardEntry;
import badgerlog.networktables.DashboardUtil;
import badgerlog.networktables.mappings.Mapping;
import badgerlog.networktables.mappings.MappingType;
import badgerlog.networktables.mappings.Mappings;
import badgerlog.networktables.publisher.DashboardPublisher;
import badgerlog.networktables.publisher.DashboardSendable;
import badgerlog.networktables.publisher.FieldPublisher;
import badgerlog.networktables.subscriber.DashboardSubscriber;
import badgerlog.networktables.subscriber.FieldSubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.event.EventLoop;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Custom method of putting data to NetworkTables. This Dashboard is meant to replace {@link edu.wpi.first.wpilibj.smartdashboard.SmartDashboard}
 * to allow for a much easier use of putting and getting values through the use of annotations.
 * <br> <br>
 * This can be done by annotating any field with a type mapping (See {@link Mappings}) with {@link Entry}.
 * Some parameters in the annotation are optional (the key will be auto set if not specified). The type of NetworkTable entry must be set.
 * <br> <br>
 * There are 3 types of Entries for use with NetworkTables. <br> <br>
 * These include <i>Publisher</i>, <i>Subscriber</i>, and <i>Sendable</i>. <br>
 * A <i>Publisher</i> puts values to NetworkTable from the field value. <br>
 * A <i>Subscriber</i> sets the field value to the value in NetworkTables. This allows it to be changed in a dashboard such as Elastic <br>
 * A <i>Sendable</i> uses an object which inherits from {@link edu.wpi.first.util.sendable.Sendable} to publish a specific set of values. <br>
 * <br>
 * If a <i>Sendable</i> can be used, it should because it logs more information than individual publishers or subscribers
 * <br> <br>
 * All fields annotated with {@link Entry} <b>must</b> be static (they are already effectively static). The can have any access level and still work.
 * All fields also must be initialized in static initialization to ensure that the default value is correct.
 * <br> <br>
 * Example: <br>
 * <code>
 * &#064;Entry(*key  = "distance", type = EntryType.Publisher) <br>
 * public static Distance distance = Inches.of(2)
 * </code> <br>
 * *key is not required, and will default to a NetworkTable key of the field name under a table with the class' simple name
 */
public class Dashboard {

    /**
     * The default table to be used by Dashboardv3
     */
    public static final NetworkTable defaultTable = NetworkTableInstance.getDefault().getTable("Dashboard");

    /**
     * The ExecutorService to use for all of Dashboardv3 for consistent thread allocations--rio doesn't support multi threading well though
     */
    public static final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Boolean representing whether the Dashboard has completed initialization
     */
    public static boolean isInitialized = false;

    private static final HashMap<String, DashboardEntry> ntEntries = new HashMap<>();
    private static final HashMap<String, DashboardPublisher<?>> singleUsePublishers = new HashMap<>();

    /**
     * Runs once on startup to create field type mappings and values on networktables
     * @param packages the packages to scan from (usually only frc.robot)
     */
    @SneakyThrows({InterruptedException.class, ExecutionException.class, IllegalAccessException.class})
    public static void initialize(String... packages) {
        defaultTable.getEntry("Dashboard Startup").setBoolean(false);
        
        var classGraph = new ClassGraph()
                .acceptPackages(packages)
                .acceptPackages("badgerlog")
                .enableAllInfo()
                .ignoreFieldVisibility();
        
        var resultAsync = classGraph.scanAsync(executorService, 10);
        var result = resultAsync.get();

        for (ClassInfo classInfo : result.getClassesWithFieldAnnotation(MappingType.class)) {
            for (FieldInfo fieldInfo : classInfo.getFieldInfo().filter(fieldInfo -> fieldInfo.hasAnnotation(MappingType.class))) {
                var field = DashboardUtil.checkFieldValidity(fieldInfo);
                
                Mappings.mappings.add((Mapping<?, ?>) field.get(null));
            }
        }

        for (ClassInfo classInfo : result.getClassesWithFieldAnnotation(Entry.class)) {
            for (FieldInfo fieldInfo : classInfo.getFieldInfo().filter(fieldInfo -> fieldInfo.hasAnnotation(Entry.class))) {
                var field = DashboardUtil.checkFieldValidity(fieldInfo);

                var entry = field.getAnnotation(Entry.class);
                var key = entry.key();
                if (key.isEmpty()) key = classInfo.getSimpleName() + "/" + fieldInfo.getName();

                String config = null;
                if (fieldInfo.hasAnnotation(Config.class)) config = field.getAnnotation(Config.class).value();

                ntEntries.put(key, switch (entry.type()) {
                    case Publisher -> new FieldPublisher<>(key, field, config);
                    case Subscriber -> new FieldSubscriber<>(key, field, config);
                    case Sendable -> new DashboardSendable(key, DashboardUtil.getFieldValue(field));
                });
            }
        }
        result.close();

        defaultTable.getEntry("Dashboard Startup").setBoolean(true);
        isInitialized = true;
    }

    /**
     * Updates all current NetworkTables entries. Should be called once in the Robot Periodic method
     */
    public static void update() {
        checkDashboardInitialized();
        ntEntries.forEach((Key, entry) -> entry.update());
    }

    /**
     * Get a button for use on NetworkTables
     *
     * @param key       the key for the button
     * @param eventLoop the {@link EventLoop} to bind the button to
     * @return the trigger bound to the NetworkTables value and {@link EventLoop}
     */
    public static Trigger getNetworkTablesButton(String key, EventLoop eventLoop) {
        checkDashboardInitialized();

        var subscriber = new DashboardSubscriber<>(key, boolean.class, NetworkTableType.kBoolean);
        return new Trigger(eventLoop, () -> subscriber.getValue(false));
    }

    /**
     * Get a button that auto resets its value to false
     *
     * @param key       the key for the button
     * @param eventLoop the {@link EventLoop} to bind the button to
     * @return the trigger bound to the NetworkTables value and {@link EventLoop} with an auto reset command
     */
    public static Trigger getAutoResettingButton(String key, EventLoop eventLoop) {
        checkDashboardInitialized();

        var subscriber = new DashboardSubscriber<>(key, boolean.class, NetworkTableType.kBoolean);
        return new Trigger(eventLoop,
                () -> subscriber.getValue(false))
                .onTrue(Commands.waitSeconds(0.25)
                        .andThen(new InstantCommand(() -> subscriber.setInitialPublish(false))
                                .ignoringDisable(true))
                );
    }

    /**
     * Put a value to NetworkTables given a key and value
     *
     * @param key         the key referencing the NetworkTable entry
     * @param value       the value to publish
     * @param <FieldType> the value of the field
     */
    public static <FieldType> void putValue(String key, FieldType value) {
        putValue(key, value, null);
    }

    /**
     * Put a value to NetworkTables given a key and value, using config to configure the mapping
     *
     * @param key         the key referencing the NetworkTable entry
     * @param value       the value to publish
     * @param config      the config string to use
     * @param <FieldType> the value of the field
     */
    @SuppressWarnings("unchecked")
    public static <FieldType> void putValue(String key, FieldType value, String config) {
        checkDashboardInitialized();
        
        DashboardPublisher<FieldType> publisher;
        if (!singleUsePublishers.containsKey(key)) {
            publisher = new DashboardPublisher<>(key, (Class<FieldType>) value.getClass(), Mappings.findMappingType(value.getClass()), config);
            singleUsePublishers.put(key, publisher);
        } else {
            publisher = (DashboardPublisher<FieldType>) singleUsePublishers.get(key);
        }

        publisher.publish(value);
    }
    
    
    private static void checkDashboardInitialized() {
        if(!isInitialized)
            throw new IllegalStateException("Dashboard is not initialized. \n Call Dashboard.initialize() in Robot.robotInit");
    }
}
