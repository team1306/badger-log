package badgerlog.processing;

import badgerlog.networktables.NTEntry;

import java.util.Map;

public record InstanceData(Map<String, NTEntry<?>> entries) {
    public void addEntry(String name, NTEntry<?> entry) {
        entries.put(name, entry);
    }
    
    public NTEntry<?> getEntry(String name) {
        return entries.get(name);
    }
    
    public boolean hasEntry(String name) {
        return entries.containsKey(name);
    }
}
