package badgerlog.annotations.configuration;

import badgerlog.annotations.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for mapping annotation types to their configuration handlers.
 * Provides static methods to register and retrieve handlers.
 */
public final class ConfigHandlerRegistry {
    private static final Map<Class<? extends Annotation>, ConfigHandler<?>> handlers = new HashMap<>();

    static {
        registerHandler(StructType.class, new StructTypeHandler());
        registerHandler(UnitConversion.class, new UnitConversionHandler());
        registerHandler(MultiUnitConversion.class, new MultiUnitConversionHandler());
        registerHandler(Key.class, new KeyHandler());
        registerHandler(AutoGenerateStruct.class, new AutoGenerateStructHandler());
    }

    private ConfigHandlerRegistry() {
    }

    /**
     * Registers a handler for a specific annotation type.
     *
     * @param annotationType The annotation class to register
     * @param handler        The handler to process the annotation
     * @param <T>            the type of the handler
     */
    public static <T extends Annotation> void registerHandler(Class<T> annotationType, ConfigHandler<T> handler) {
        handlers.put(annotationType, handler);
    }

    /**
     * Retrieves the handler for a given annotation type.
     *
     * @param annotationType The annotation class to look up
     * @param <T>            the type of the handler
     * @return The associated handler, or null if not found
     */
    @SuppressWarnings("unchecked") // Cast not possible to fail, since handlers are registered with the same type
    public static <T extends Annotation> ConfigHandler<T> getHandler(Class<T> annotationType) {
        return (ConfigHandler<T>) handlers.get(annotationType);
    }

    /**
     * Checks if a valid handler exists for the annotation type.
     *
     * @param annotationType The annotation class to check
     * @return true if a non-null handler is registered, false otherwise
     */
    public static boolean hasValidHandler(Class<?> annotationType) {
        return annotationType != null && handlers.containsKey(annotationType) && handlers.get(annotationType) != null;
    }
}