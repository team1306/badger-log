package badgerlog.networktables.publisher;


import badgerlog.networktables.DashboardEntry;
import badgerlog.networktables.DashboardUtil;
import badgerlog.networktables.mappings.Mappings;

import java.lang.reflect.Field;

/**
 * Class to publish a field with a configuration to NetworkTables
 * @param <FieldType> the type of the field
 */
@SuppressWarnings("unchecked")
public class FieldPublisher<FieldType> extends DashboardPublisher<FieldType> implements DashboardEntry {

    private final Field field;

    /**
     * Construct a new FieldPublisher, updating NetworkTables based off the field value
     * @param key the key for NetworkTables
     * @param field the field to update from
     * @param config the configuration for the {@link Mappings}
     */
    public FieldPublisher(String key, Field field, String config) {
        super(key, (Class<FieldType>) field.getType(), Mappings.findMappingType(field.getType()), config);

        this.field = field;
    }

    @Override
    public void update() {
        super.publish(DashboardUtil.getFieldValue(field));
    }
}
