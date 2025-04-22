package badgerlog.entry.configuration;

import badgerlog.entry.configuration.handlers.*;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;
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

    public static <T extends Annotation> void registerHandler(@Nonnull Class<T> annotationType, @Nonnull ConfigHandler<T> handler) {
        handlers.put(annotationType, handler);
    }

    @SuppressWarnings("unchecked") // Cast not possible to fail, since handlers are registered with the same type
    public static <T extends Annotation> ConfigHandler<T> getHandler(@Nonnull Class<T> annotationType) {
        return (ConfigHandler<T>) handlers.get(annotationType);
    }

    @Contract("null -> false")
    public static boolean hasValidHandler(Class<?> annotationType) {
        return annotationType != null && handlers.containsKey(annotationType) && handlers.get(annotationType) != null;
    }
}