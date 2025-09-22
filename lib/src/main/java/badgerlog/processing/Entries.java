package badgerlog.processing;

import java.lang.reflect.Field;
import java.util.Map;

public record Entries(Map<Class<?>, ClassData> classDataMap) {
    public void put(Class<?> type, ClassData data) {
        classDataMap.putIfAbsent(type, data);
    }
    
    public ClassData getClassData(Class<?> type) {
        return classDataMap.get(type);
    }
    
    public InstanceData getInstanceEntries(Class<?> clazz, Object instance) {
        return classDataMap.get(clazz).instanceEntries().get(instance);
    }
    
    public Map<String, Field> getFieldMap(Class<?> type) {
        return Map.copyOf(classDataMap.get(type).fieldMap());
    }
}
