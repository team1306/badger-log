package badgerlog.processing.data;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

/**
 * Holds the data used for an entire class with entries inside. Contains all instances of the class.
 */
public record ClassData(Map<String, Field> fieldMap, Map<Object, InstanceData> instanceEntries) {
    /**
     * Constructs a new ClassData given two maps for fields and instances.
     *
     * @param fieldMap the map of strings to fields to use
     * @param instanceEntries the map of instances to {@link InstanceData}
     */
    public ClassData {
    }

    /**
     * Adds a field to the map of fields in the class.
     *
     * @param field the field to add to the map
     */
    public void addField(Field field) {
        fieldMap.put(field.getName(), field);
    }

    /**
     * {@return the number of instances currently present for the class}
     */
    public int getInstanceCount() {
        return Math.toIntExact(instanceEntries().keySet().stream().filter(Objects::nonNull).count());
    }
}
