package frc.robot.util.dashboardv3.networktables.publisher;

import edu.wpi.first.networktables.GenericPublisher;
import edu.wpi.first.networktables.NetworkTableType;
import frc.robot.util.dashboardv3.Dashboard;
import frc.robot.util.dashboardv3.networktables.mappings.Mapping;
import frc.robot.util.dashboardv3.networktables.mappings.Mappings;

/**
 * This class represents a publisher for values to be sent to NetworkTables. 
 * Publishing values to NetworkTables can be done at any point during the lifespan of this class
 * @param <FieldType> the type of field to map to a valid NetworkTableType
 */
public class DashboardPublisher<FieldType> {

    private final GenericPublisher publisher;
    private final Mapping<FieldType, Object> mapping;
    private final String config;

    public DashboardPublisher(String key, Class<FieldType> type, NetworkTableType targetType, String config) {
        mapping = Mappings.findMapping(type);
        this.config = config;

        publisher = Dashboard.defaultTable
                .getEntry(key)
                .getTopic()
                .genericPublish(targetType.getValueStr());
    }

    public DashboardPublisher(String key, Class<FieldType> type, NetworkTableType targetType) {
        this(key, type, targetType, null);
    }

    /**
     * Publish a value to NetworkTables. Maps the input to a valid type
     * @param input the input value
     */
    public void publish(FieldType input) {
        publisher.setValue(mapping.toNT(input, config));
    }
}
