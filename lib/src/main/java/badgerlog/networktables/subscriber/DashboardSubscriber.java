package badgerlog.networktables.subscriber;


import badgerlog.Dashboard;
import badgerlog.networktables.mappings.Mapping;
import badgerlog.networktables.mappings.Mappings;
import badgerlog.networktables.publisher.DashboardPublisher;
import edu.wpi.first.networktables.GenericSubscriber;
import edu.wpi.first.networktables.NetworkTableType;
import lombok.Setter;

/**
 * This class represents a subscriber for values to be gotten from NetworkTables
 * Subscribing to values from NetworkTables can be done at any point, allowing for continuous subscribing or only a single time
 * @param <FieldType> the type of the field to have the NetworkTablesType mapped to
 */
public class DashboardSubscriber<FieldType> {

    private final DashboardPublisher<FieldType> publisher;
    private final GenericSubscriber subscriber;
    private final Mapping<FieldType, Object> mapping;
    private final String config;

    /**
     * Whether the initial publish of the subscriber was completed
     * @param initialPublish the value to set initialPublish to
     */
    @Setter
    private boolean initialPublish = false;

    /**
     * Default constructor of DashboardSubscriber
     * @param key the key on NetworkTables
     * @param type the type of the original type
     * @param targetType the target {@link NetworkTableType}
     * @param config the configuration options for use with the {@link Mappings}
     */
    public DashboardSubscriber(String key, Class<FieldType> type, NetworkTableType targetType, String config) {
        mapping = Mappings.findMapping(type);
        this.config = config;

        publisher = new DashboardPublisher<>(key, type, targetType, config);

        subscriber = Dashboard.defaultTable
                .getEntry(key)
                .getTopic()
                .genericSubscribe(targetType.getValueStr());
    }

    /**
     * Constructor to use when there is no configuration option
     * @param key the key for NetworkTables
     * @param type the type of the original object
     * @param targetType the target {@link NetworkTableType}
     */
    public DashboardSubscriber(String key, Class<FieldType> type, NetworkTableType targetType) {
        this(key, type, targetType, null);
    }

    /**
     * Get a value from NetworkTables, publishing one if it doesn't exist
     * @param defaultValue the default value to use
     * @return the value at the NetworkTable key
     */
    public FieldType getValue(FieldType defaultValue) {
        if (!initialPublish) {
            initialPublish = true;
            publisher.publish(defaultValue);
        }

        var value = subscriber.get();
        if (value.getType() == NetworkTableType.kUnassigned || value.getValue() == null) return defaultValue;

        return mapping.toField(value.getValue(), config);
    }
}
