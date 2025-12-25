package badgerlog.processing.data;

import java.util.Map;
import java.util.Objects;

/**
 * Holds the data used for an entire class with entries inside. Contains all instances of the class.
 */
public final class ClassData {
    private final Map<Object, InstanceData> instanceEntries;

    private int instanceCount = 0;
    /**
     * Constructs a new ClassData given two maps for fields and instances.
     *
     * @param instanceEntries the map of instances to {@link InstanceData}
     */
    public ClassData(Map<Object, InstanceData> instanceEntries) {
        this.instanceEntries = instanceEntries;
    }
    
    public int getInstanceCount() {
        return instanceCount;
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
        return Objects.equals(this.instanceEntries, that.instanceEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceEntries);
    }

    @Override
    public String toString() {
        return "ClassData[" + "instanceEntries=" + instanceEntries + ']';
    }
}
