package badgerlog.entry;

import badgerlog.StructOptions;
import badgerlog.networktables.mappings.conversion.UnitConverter;
import edu.wpi.first.units.Unit;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * Container class for storing configuration settings and unit converters.
 * Supports chained method calls for building configurations.
 */
public class Configuration {
    private final HashMap<String, String> configurations = new HashMap<>();
    private final HashMap<String, UnitConverter<?>> converters = new HashMap<>();
    @Getter
    private String key = null;
    @Getter
    private StructOptions structOptions = null;

    public static Configuration defaultConfiguration = new Configuration();


    /**
     * Retrieves a unit converter by its identifier.
     *
     * @param id The converter identifier (empty string for default)
     * @return The associated converter, or null if not found
     */
    @SuppressWarnings("unchecked")
    // can guarantee that the resulting converter is used by the correct mapping type, since it is defined in the mapping 
    public <T extends Unit> @Nullable UnitConverter<T> getConverter(String id) {
        return (UnitConverter<T>) converters.get(id);
    }

    /**
     * Retrieves the default unit converter.
     *
     * @return The default converter, or null if not set
     */
    public <T extends Unit> @Nullable UnitConverter<T> getDefaultConverter() {
        return getConverter("");
    }

    /**
     * Gets a configuration value by key.
     *
     * @param key The configuration key
     * @return The associated value, or null if not found
     */
    public @Nullable String getConfiguration(@Nonnull String key) {
        return configurations.get(key);
    }

    /**
     * Sets the configuration key and returns the modified instance.
     *
     * @param key The key to set
     * @return This configuration object
     */
    public Configuration withKey(@Nullable String key) {
        this.key = key;
        return this;
    }

    /**
     * Adds a unit converter and returns the modified instance.
     *
     * @param id        The converter identifier
     * @param converter The converter to add
     * @return This configuration object
     */
    public Configuration withConverter(@Nonnull String id, @Nullable UnitConverter<?> converter) {
        this.converters.put(id, converter);
        return this;
    }

    /**
     * Adds struct options and returns the modified instance.
     *
     * @param structOptions The struct options to set
     * @return This configuration object
     */
    public Configuration withStructOptions(@Nullable StructOptions structOptions) {
        this.structOptions = structOptions;
        return this;
    }

    /**
     * Adds a key-value configuration pair and returns the modified instance.
     *
     * @param key   The configuration key
     * @param value The configuration value
     * @return This configuration object
     */
    public Configuration withConfiguration(@Nonnull String key, @Nullable String value) {
        this.configurations.put(key, value);
        return this;
    }
}
