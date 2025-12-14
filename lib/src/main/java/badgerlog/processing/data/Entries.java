package badgerlog.processing.data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Contains all the generated entry data.
 *
 * @param classDataMap the map of classes to {@link ClassData} to use
 */
public record Entries(Map<Class<?>, ClassData> classDataMap) {
    /**
     * Adds a {@link ClassData} to the map of classes to {@code ClassData}.
     *
     * @param type the type the {@code ClassData} is holding the data for
     * @param data the data to add to the map
     */
    public void put(Class<?> type, ClassData data) {
        classDataMap.putIfAbsent(type, data);
    }

    /**
     * Gets a {@link ClassData} given a class.
     *
     * @param type the type to find the {@code ClassData} for
     *
     * @return the {@code ClassData} associated with the type
     */
    public ClassData getClassData(Class<?> type) {
        return classDataMap.get(type);
    }

    /**
     * Gets the specified instance entry associated with a particular class.
     *
     * <p>It returns an empty {@link InstanceData} if not present and doesn't add it to anything </p>
     *
     * @param clazz the class to get the instance from
     * @param instance the instance to get the data from
     *
     * @return the {@code InstanceData} associated with the specific class and instance
     */
    public InstanceData getInstanceEntries(Class<?> clazz, Object instance) {
        ClassData classData = getClassData(clazz);
        if (classData == null) {
            return new InstanceData(new HashMap<>());
        }
        return classData.instanceEntries().get(instance);
    }

    /**
     * Adds an empty instance to the list of {@link InstanceData} within the specified class's {@link ClassData}
     *
     * @param clazz the class to add the instance to
     * @param instance the instance to add
     */
    public void addInstance(Class<?> clazz, Object instance) {
        ClassData classData = getClassData(clazz);
        if (classData == null) {
            classData = new ClassData(new HashMap<>(), new WeakHashMap<>());
            classDataMap.put(clazz, classData);
        }
        classData.incrementInstanceCount();
        classData.instanceEntries().putIfAbsent(instance, new InstanceData(new HashMap<>()));
    }

    /**
     * Gets the field map for a particular class
     *
     * @param type the class to get the field map from
     *
     * @return a copied map containing the entries from the {@link ClassData} field map
     */
    public Map<String, Field> getFieldMap(Class<?> type) {
        return Map.copyOf(classDataMap.get(type).fieldMap());
    }
}
