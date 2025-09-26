package badgerlog.processing;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.Field;
import java.util.Map;

@Getter
@EqualsAndHashCode
@ToString
public final class ClassData {
    private final Map<String, Field> fieldMap;
    private final Map<Object, InstanceData> instanceEntries;
    private int instanceCount = 0;

    public ClassData(Map<String, Field> fieldMap, Map<Object, InstanceData> instanceEntries) {
        this.fieldMap = fieldMap;
        this.instanceEntries = instanceEntries;
    }

    public void addField(Field field) {
        fieldMap.put(field.getName(), field);
    }

    public void incrementInstanceCount(){
        instanceCount++;
    }
}
