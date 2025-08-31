package badgerlog.processing;

import badgerlog.networktables.NT;
import badgerlog.networktables.NTEntry;
import badgerlog.networktables.NTUpdatable;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

/**
 * A modification of a {@link HashMap} that, when a key's value is changed, closes the previous entry on NetworkTables.
 */
public final class CheckedNetworkTablesMap extends HashMap<String, NT> {

    /**
     * Closes the previous NetworkTables entry if it is going to be overwritten.
     *
     * @see Map#put(Object, Object)
     */
    @SneakyThrows
    @Override
    public NT put(String key, NT value) {
        if (containsKey(key)) {
            NT oldValue = this.get(key);
            if (oldValue instanceof AutoCloseable closeable) {
                closeable.close();
            }
        }

        return super.put(key, value);
    }

    /**
     * Closes the NetworkTables entry before removing.
     *
     * @see Map#remove(Object)
     */
    @SneakyThrows
    @Override
    public NT remove(Object key) {
        if (containsKey(key)) {
            NT oldValue = this.get(key);
            if (oldValue instanceof AutoCloseable closeable) {
                closeable.close();
            }
        }
        return super.remove(key);
    }

    /**
     * Finds all the values in the map that also implement {@link NTUpdatable}.
     *
     * @return a map containing the keys associated with each NTUpdatable
     */
    public Map<String, NTUpdatable> getUpdaters() {
        return this.entrySet()
                .stream()
                .filter(entry -> entry.getValue() instanceof NTUpdatable)
                .collect(
                        HashMap::new,
                        (map, entry) -> map.put(entry.getKey(), (NTUpdatable) entry.getValue()),
                        HashMap::putAll);
    }

    /**
     * Finds the value that implements {@link NTEntry}, or null if not present.
     *
     * @param key the key used in the Map
     *
     * @return the value that implements NTEntry, or null if not present or doesn't implement NTEntry
     */
    public NTEntry<?> getNTEntry(String key) {
        NT value = super.get(key);
        if (value instanceof NTEntry<?> entry) {
            return entry;
        }
        return null;
    }
}
