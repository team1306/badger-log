package badgerlog.annotations.configuration;

import badgerlog.annotations.*;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

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

    public static <T extends Annotation> void registerHandler(Class<T> annotationType, ConfigHandler<T> handler) {
        handlers.put(annotationType, handler);
    }

    @SuppressWarnings("unchecked") // Cast not possible to fail, since handlers are registered with the same type
    public static <T extends Annotation> ConfigHandler<T> getHandler(Class<T> annotationType) {
        return (ConfigHandler<T>) handlers.get(annotationType);
    }

    public static boolean hasValidHandler(Class<?> annotationType) {
        return annotationType != null && handlers.containsKey(annotationType) && handlers.get(annotationType) != null;
    }
}