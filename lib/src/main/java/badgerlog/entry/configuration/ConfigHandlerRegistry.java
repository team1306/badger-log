package badgerlog.entry.configuration;

import badgerlog.entry.configuration.handlers.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandlerRegistry {
    private static final Map<Class<? extends Annotation>, ConfigHandler<?>> handlers = new HashMap<>();

    static {
        registerHandler(StructType.class, new StructTypeHandler());
        registerHandler(UnitConversion.class, new UnitConversionHandler());
        registerHandler(MultiUnitConversion.class, new MultiUnitConversionHandler());
        registerHandler(Key.class, new KeyHandler());
    }

    public static <T extends Annotation> void registerHandler(Class<T> annotationType, ConfigHandler<T> handler) {
        handlers.put(annotationType, handler);
    }

    public static <T extends Annotation> ConfigHandler<T> getHandler(Class<T> annotationType) {
        return (ConfigHandler<T>) handlers.get(annotationType);
    }

    public static boolean hasValidHandler(Class<?> annotationType) {
        return handlers.containsKey(annotationType) && handlers.get(annotationType) != null;
    }
}