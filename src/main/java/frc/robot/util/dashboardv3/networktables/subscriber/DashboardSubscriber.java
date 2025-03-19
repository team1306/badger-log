package frc.robot.util.dashboardv3.networktables.subscriber;


import edu.wpi.first.networktables.GenericSubscriber;
import edu.wpi.first.networktables.NetworkTableType;
import frc.robot.util.dashboardv3.Dashboard;
import frc.robot.util.dashboardv3.networktables.mappings.Mapping;
import frc.robot.util.dashboardv3.networktables.mappings.Mappings;
import frc.robot.util.dashboardv3.networktables.publisher.DashboardPublisher;
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

    @Setter
    private boolean initialPublish = false;

    public DashboardSubscriber(String key, Class<FieldType> type, NetworkTableType targetType, String config) {
        mapping = Mappings.findMapping(type);
        this.config = config;

        publisher = new DashboardPublisher<>(key, type, targetType, config);

        subscriber = Dashboard.defaultTable
                .getEntry(key)
                .getTopic()
                .genericSubscribe(targetType.getValueStr());
    }

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
