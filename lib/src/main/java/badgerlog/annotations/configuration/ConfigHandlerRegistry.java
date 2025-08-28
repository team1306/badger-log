package badgerlog.annotations.configuration;

import badgerlog.annotations.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * Internal class used to register handlers for specific annotations.
 */
public final class ConfigHandlerRegistry {
    private static final Map<Class<? extends Annotation>, ConfigHandler<?>> handlers = new HashMap<>();

    static {
        registerHandler(Struct.class, new StructTypeHandler());
        registerHandler(UnitConversion.class, new UnitConversionHandler());
        registerHandler(MultiUnitConversion.class, new MultiUnitConversionHandler());
        registerHandler(Key.class, new KeyHandler());
        registerHandler(AutoGenerateStruct.class, new AutoGenerateStructHandler());
    }

    private ConfigHandlerRegistry() {
    }

    /**
     * Registers a annotation handler.
     *
     * @param annotationType the class representing the annotation
     * @param handler        the handler to register
     * @param <T>            the type of the annotation
     */
    public static <T extends Annotation> void registerHandler(Class<T> annotationType, ConfigHandler<T> handler) {
        handlers.put(annotationType, handler);
    }

    /**
     * Gets an annotation handler from the map using the annotation's class.
     * @param annotationType the type of the annotation
     * @return the handler associated with the annotation
     * @param <T> the type of the annotation
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