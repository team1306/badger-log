package badgerlog.processing.data;

import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

/**
 * Holds the data used for an entire class with entries inside. Contains all instances of the class.
 */
public final class ClassData {
    private final Map<String, Field> fieldMap;
    private final Map<Object, InstanceData> instanceEntries;

    @Getter
    private int instanceCount = 0;
    /**
     * Constructs a new ClassData given two maps for fields and instances.
     *
     * @param fieldMap the map of strings to fields to use
     * @param instanceEntries the map of instances to {@link InstanceData}
     */
    public ClassData(Map<String, Field> fieldMap, Map<Object, InstanceData> instanceEntries) {
        this.fieldMap = fieldMap;
        this.instanceEntries = instanceEntries;
    }

    /**
     * Adds a field to the map of fields in the class.
     *
     * @param field the field to add to the map
     */
    public void addField(Field field) {
        fieldMap.put(field.getName(), field);
    }

    public Map<String, Field> fieldMap() {
        return fieldMap;
    }

    public Map<Object, InstanceData> instanceEntries() {
        return instanceEntries;
    }

    public void incrementInstanceCount() {
        instanceCount++;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (ClassData) obj;
        return Objects.equals(this.fieldMap, that.fieldMap) && Objects
                .equals(this.instanceEntries, that.instanceEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldMap, instanceEntries);
    }

    @Override
    public String toString() {
        return "ClassData[" + "fieldMap=" + fieldMap + ", " + "instanceEntries=" + instanceEntries + ']';
    }
}
