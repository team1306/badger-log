package badgerlog.annotations.configuration;

import badgerlog.annotations.Convert;
import badgerlog.annotations.Key;
import badgerlog.annotations.Struct;
import badgerlog.annotations.Table;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * Registers handlers for specific annotations to be used when generating a configuration.
 */
public final class ConfigHandlerRegistry {
    private static final Map<Class<? extends Annotation>, ConfigHandler<?>> handlers = new HashMap<>();

    static {
        registerHandler(Struct.class, new StructTypeHandler());
        registerHandler(Convert.class, new ConvertHandler());
        registerHandler(Key.class, new KeyHandler());
        registerHandler(Table.class, new TableHandler());
    }

    private ConfigHandlerRegistry() {
    }

    /**
     * Registers an annotation handler.
     *
     * @param annotationType the class representing the annotation
     * @param handler the handler to register
     * @param <T> the type of the annotation
     */
    public static <T extends Annotation> void registerHandler(Class<T> annotationType, ConfigHandler<T> handler) {
        handlers.put(annotationType, handler);
    }

    /**
     * Gets an annotation handler from the map using the annotation's class.
     *
     * @param annotationType the type of the annotation
     * @param <T> the type of the annotation
     *
     * @return the handler associated with the annotation
     */
    @SuppressWarnings("unchecked") // Cast not possible to fail, since handlers are registered with the same type
    public static <T extends Annotation> ConfigHandler<T> getHandler(Class<T> annotationType) {
        return (ConfigHandler<T>) handlers.get(annotationType);
    }

    /**
     * {@return whether or not the map contains a valid handler for the specified annotation type}
     *
     * @param annotationType the type of the annotation
     */
    public static boolean hasValidHandler(Class<? extends Annotation> annotationType) {
        return annotationType != null && handlers.containsKey(annotationType) && handlers.get(annotationType) != null;
    }
}
