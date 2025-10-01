package badgerlog.processing.data;

import badgerlog.networktables.NTEntry;

import java.util.Map;

/**
 * Holds the data for an instance's entries.
 *
 * @param entries the map of field names to NetworkTable entries
 */
public record InstanceData(Map<String, NTEntry<?>> entries) {

    /**
     * Creates a new InstanceData with an initial set of entries to use.
     *
     * @param entries the map of field names to NetworkTable entries
     */
    public InstanceData {

    }

    /**
     * Adds an entry to the map of field names to NetworkTable entries.
     *
     * @param name the name of the field
     * @param entry the entry on NetworkTables
     */
    public void addEntry(String name, NTEntry<?> entry) {
        entries.put(name, entry);
    }

    /**
     * Gets an entry from the map of field names to NetworkTable entries.
     *
     * @param name the name of the field to get the entry
     *
     * @return the entry corresponding to the specific field name
     */
    public NTEntry<?> getEntry(String name) {
        return entries.get(name);
    }

    /**
     * Checks the map of field names to NetworkTable entries for the specified field's name
     *
     * @param name the name of the field to check
     *
     * @return if the entry is present or not
     */
    public boolean hasEntry(String name) {
        return entries.containsKey(name);
    }
}
