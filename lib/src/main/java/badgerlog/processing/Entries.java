package badgerlog.processing;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public record Entries(Map<Class<?>, ClassData> classDataMap) {
    public void put(Class<?> type, ClassData data) {
        classDataMap.putIfAbsent(type, data);
    }
    
    public ClassData getClassData(Class<?> type) {
        return classDataMap.get(type);
    }
    
    //returns an empty one if not present, does not create one 
    public InstanceData getInstanceEntries(Class<?> clazz, Object instance) {
        ClassData classData = getClassData(clazz);
        if(classData == null) {
            return new InstanceData(new HashMap<>());
        }
        return classData.getInstanceEntries().get(instance);
    }
    
    public void addInstance(Class<?> clazz, Object instance) {
        ClassData classData = getClassData(clazz);
        if(classData == null) {
            classData = new ClassData(new HashMap<>(), new HashMap<>());
            classDataMap.put(clazz, classData);
        }
        
        classData.getInstanceEntries().putIfAbsent(instance, new InstanceData(new HashMap<>()));
        
        if(instance != null){
            classData.incrementInstanceCount();
        }
    }
    
    public Map<String, Field> getFieldMap(Class<?> type) {
        return Map.copyOf(classDataMap.get(type).getFieldMap());
    }
}
