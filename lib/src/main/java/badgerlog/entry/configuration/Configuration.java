package badgerlog.entry.configuration;

import badgerlog.StructOptions;
import badgerlog.networktables.mappings.conversion.UnitConverter;
import edu.wpi.first.units.Unit;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.HashMap;

public class Configuration {
    private final HashMap<String, String> configurations = new HashMap<>();
    private final HashMap<String, UnitConverter<?>> converters = new HashMap<>();
    @Getter
    private String key = null;
    @Getter
    private StructOptions structOptions = null;

    public static Configuration defaultConfiguration = new Configuration();
    

    @SuppressWarnings("unchecked") // can guarantee that the resulting converter is used by the correct mapping type, since it is defined in the mapping 
    public <T extends Unit> @Nullable UnitConverter<T> getConverter(String id) {
        return (UnitConverter<T>) converters.get(id);
    }

    public <T extends Unit> @Nullable UnitConverter<T> getDefaultConverter() {
        return getConverter("");
    }
    
    public @Nullable String getConfiguration(String key) {
        return configurations.get(key);
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
