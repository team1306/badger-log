package badgerlog.processing;

import badgerlog.networktables.NT;
import badgerlog.networktables.NTCloseable;
import badgerlog.networktables.NTEntry;
import badgerlog.networktables.Updater;

import java.util.HashMap;
import java.util.Map;

public class CheckedNetworkTablesMap extends HashMap<String, NT> {

    @Override
    public NT put(String key, NT value) {
        if (containsKey(key)) {
            NT oldValue = this.get(key);
            if (oldValue instanceof NTCloseable closeable) {
                closeable.close();
            }
        }

        return super.put(key, value);
    }

    @Override
    public NT remove(Object key) {
        if (containsKey(key)) {
            NT oldValue = this.get(key);
            if (oldValue instanceof NTCloseable closeable) {
                closeable.close();
            }
        }
        return super.remove(key);
    }

    public Map<String, Updater> getUpdaters() {
        return this.entrySet()
                .stream()
                .filter(entry -> entry.getValue() instanceof Updater)
                .collect(
                        HashMap::new,
                        (map, entry) -> map.put(entry.getKey(), (Updater) entry.getValue()),
                        HashMap::putAll);
    }

    public NTEntry<?> getNTEntry(String key) {
        NT value = super.get(key);
        if (value instanceof NTEntry<?> entry) {
            return entry;
        }
        return null;
    }
}
