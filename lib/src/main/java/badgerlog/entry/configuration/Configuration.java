package badgerlog.entry.configuration;

import badgerlog.StructOptions;
import badgerlog.networktables.mappings.conversion.UnitConverter;
import edu.wpi.first.units.Unit;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Configuration {
    private final Map<String, String> configurations = new HashMap<>();
    private final HashMap<String, UnitConverter<?>> converters = new HashMap<>();
    private String key = null;
    private StructOptions structOptions = null;

    public <T extends Unit> @Nullable UnitConverter<T> getConverter(String id) {
        return (UnitConverter<T>) converters.get(id);
    }

    public <T extends Unit> @Nullable UnitConverter<T> getDefaultConverter() {
        return getConverter("");
    }

    public Configuration withKey(String key) {
        this.key = key;
        return this;
    }

    public Configuration withConverter(String id, UnitConverter<?> converter) {
        this.converters.put(id, converter);
        return this;
    }

    public Configuration withStructOptions(StructOptions structOptions) {
        this.structOptions = structOptions;
        return this;
    }


    public Configuration withConfiguration(String key, String value) {
        this.configurations.put(key, value);
        return this;
    }
}
