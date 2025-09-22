package badgerlog.processing;

import java.lang.reflect.Field;
import java.util.Map;

public record ClassData(Map<String, Field> fieldMap, Map<Object, InstanceData> instanceEntries) {
    public void addField(Field field) {
        fieldMap.put(field.getName(), field);
    }
}
