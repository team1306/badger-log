package badgerlog.networktables.publisher;

import badgerlog.Dashboard;
import badgerlog.networktables.mappings.Mapping;
import badgerlog.networktables.mappings.Mappings;
import edu.wpi.first.networktables.GenericPublisher;
import edu.wpi.first.networktables.NetworkTableType;

/**
 * This class represents a publisher for values to be sent to NetworkTables. 
 * Publishing values to NetworkTables can be done at any point during the lifespan of this class
 * @param <FieldType> the type of field to map to a valid NetworkTableType
 */
public class DashboardPublisher<FieldType> {

    private final GenericPublisher publisher;
    private final Mapping<FieldType, Object> mapping;
    private final String config;

    /**
     * Default constructor of Dashboard Publisher
     * @param key the key on NetworkTables
     * @param type the type of the original type
     * @param targetType the target {@link NetworkTableType}
     * @param config the configuration options for use with the {@link Mappings}
     */
    public DashboardPublisher(String key, Class<FieldType> type, NetworkTableType targetType, String config) {
        mapping = Mappings.findMapping(type);
        this.config = config;

        publisher = Dashboard.defaultTable
                .getEntry(key)
                .getTopic()
                .genericPublish(targetType.getValueStr());
    }

    /**
     * Constructor to use when there is no configuration option
     * @param key the key for NetworkTables
     * @param type the type of the original object
     * @param targetType the target {@link NetworkTableType}
     */
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
