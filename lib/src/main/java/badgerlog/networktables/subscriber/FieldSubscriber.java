package badgerlog.networktables.subscriber;

import badgerlog.networktables.DashboardEntry;
import badgerlog.networktables.DashboardUtil;
import badgerlog.networktables.mappings.Mappings;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * Class to subscribe to a NetworkTables value and update the field
 *
 * @param <FieldType> the type of the field
 */
@SuppressWarnings("unchecked")
public class FieldSubscriber<FieldType> extends DashboardSubscriber<FieldType> implements DashboardEntry {

    private final Field field;

    /**
     * Construct a new FieldSubscriber, updating the field based off NetworkTables
     *
     * @param key    the key on NetworkTables
     * @param field  the field to update
     * @param config the configuration for the {@link Mappings}
     */
    public FieldSubscriber(String key, Field field, String config) {
        super(key, (Class<FieldType>) field.getType(), Mappings.findMappingType(field.getType()), config);
        this.field = field;
    }

    @Override
    @SneakyThrows({IllegalAccessException.class, IllegalArgumentException.class})
    public void update() {
        DashboardUtil.setFieldValue(field, super.getValue((FieldType) field.get(null)));
    }
}
