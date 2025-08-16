package badgerlog.annotations.configuration;

import badgerlog.annotations.StructOptions;
import badgerlog.conversion.UnitConverter;
import edu.wpi.first.units.Unit;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Container class for storing configuration settings and unit converters.
 * Supports chained method calls for building configurations.
 */
@SuppressWarnings("JavadocDeclaration")
public class Configuration {
    private final HashMap<String, UnitConverter<?>> converters = new HashMap<>();
    /**
     * The key for NetworkTables
     *
     * @return the key used for the entry on NetworkTables
     */
    @Getter
    private String key = null;

    /**
     * The method for publishing/subscribing structs to NetworkTables
     *
     * @return the method used for struct handling
     */
    @Getter
    private StructOptions structOptions = null;

    /**
     * Generates a {@link Configuration} by processing annotations on a field.
     * Uses registered handlers from {@link ConfigHandlerRegistry} to interpret annotations.
     *
     * @param field the annotated field to process
     * @return a configuration populated with data from the field's annotations
     */
    public static Configuration createConfigurationFromField(Field field) {
        Configuration config = new Configuration();
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            handleAnnotation(annotation, config);
        }

        String key;
        if (config.getKey() == null || config.getKey().isBlank())
            key = field.getDeclaringClass().getSimpleName() + "/" + field.getName();
        else key = config.getKey();

        return config.withKey(key);
    }

    @SuppressWarnings("unchecked") // Annotation must have a class of type T from type requirements
    private static <T extends Annotation> void handleAnnotation(T annotation, Configuration config) {
        if (!ConfigHandlerRegistry.hasValidHandler(annotation.annotationType())) return;
        ConfigHandlerRegistry.getHandler((Class<T>) annotation.annotationType()).process(annotation, config);
    }

    /**
     * Retrieves a unit converter by its identifier.
     *
     * @param id  The converter identifier (empty string for default)
     * @param <T> the type for the converter to be cast to
     * @return The associated converter, or null if not found
     */
    @SuppressWarnings("unchecked")
    // can guarantee that the resulting converter is used by the correct mapping type, since it is defined in the mapping 
    public <T extends Unit> UnitConverter<T> getConverter(String id) {
        return (UnitConverter<T>) converters.get(id);
    }

    /**
     * Retrieves the default unit converter.
     *
     * @param <T> the type for the converter to be cast to
     * @return The default converter, or null if not set
     */
    public <T extends Unit> UnitConverter<T> getDefaultConverter() {
        return getConverter("");
    }

    /**
     * Sets the configuration key and returns the modified instance.
     *
     * @param key The key to set
     * @return This configuration object
     */
    public Configuration withKey(String key) {
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
    public Configuration withConverter(String id, UnitConverter<?> converter) {
        this.converters.put(id, converter);
        return this;
    }

    /**
     * Adds struct options and returns the modified instance.
     *
     * @param structOptions The struct options to set
     * @return This configuration object
     */
    public Configuration withStructOptions(StructOptions structOptions) {
        this.structOptions = structOptions;
        return this;
    }
}
