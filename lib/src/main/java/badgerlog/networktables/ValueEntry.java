package badgerlog.networktables;

import badgerlog.Dashboard;
import badgerlog.annotations.configuration.Configuration;
import badgerlog.conversion.Mapping;
import badgerlog.conversion.Mappings;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableType;

public final class ValueEntry<T> implements NTEntry<T> {

    private final Configuration config;

    private final Mapping<T, Object> fieldValueMapping;
    private final GenericEntry entry;
    private final String key;

    public ValueEntry(String key, Class<T> fieldTypeClass, T initialValue, Configuration config) {
        this.config = config;
        this.key = key;

        this.fieldValueMapping = Mappings.findMapping(fieldTypeClass);

        NetworkTableType networkTableType = Mappings.findMappingType(fieldTypeClass);
        this.entry = Dashboard.defaultTable.getEntry(key).getTopic().getGenericEntry(networkTableType.getValueStr());

        publishValue(initialValue);
    }

    @Override
    public void publishValue(T value) {
        entry.setValue(fieldValueMapping.toNT(value, config));
    }

    @Override
    public T retrieveValue() {
        return fieldValueMapping.toStart(entry.get().getValue(), config);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void close() {
        entry.unpublish();
        entry.close();
    }
}
