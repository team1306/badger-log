package badgerlog;

import badgerlog.entry.ConfigHandlerRegistry;
import badgerlog.entry.Configuration;
import io.github.classgraph.FieldInfo;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Utility class for internal BadgerLog operations involving field manipulation via reflection.
 * Provides methods to get/set static field values, validate field constraints (static, non-final, initialized),
 * and generate {@link Configuration} objects by processing field annotations using registered handlers.
 * <p>
 * This class is not meant to be instantiated and operates exclusively using static methods.
 */
public final class DashboardUtil {

    private DashboardUtil() {
    }

    /**
     * Retrieves the value of a static field via reflection.
     *
     * @param field the static field to read
     * @return the current value of the field
     */
    @SneakyThrows({IllegalAccessException.class, IllegalArgumentException.class})
    public static Object getFieldValue(@Nonnull Field field) {
        field.setAccessible(true);
        return field.get(null);
    }

    /**
     * Sets the value of a static field via reflection.
     *
     * @param field the static field to modify
     * @param value the new value to assign
     */
    @SneakyThrows({IllegalAccessException.class})
    public static void setFieldValue(@Nonnull Field field, Object value) {
        field.setAccessible(true);
        field.set(null, value);
    }

    /**
     * Validates that a field meets BadgerLog requirements: static, non-final, and initialized.
     *
     * @param fieldInfo the field metadata to validate
     * @return the validated {@link Field} object
     * @throws IllegalStateException if the field is non-static, final, or uninitialized
     */
    public static Field checkFieldValidity(@Nonnull FieldInfo fieldInfo) {
        var field = fieldInfo.loadClassAndGetField();
        if (!fieldInfo.isStatic())
            throw new IllegalStateException("The field " + fieldInfo.getName() + " in " + fieldInfo.getClassName() + " must be static");
        if (fieldInfo.isFinal())
            throw new IllegalStateException("The field " + fieldInfo.getName() + " in " + fieldInfo.getClassName() + " must not be final");
        if (DashboardUtil.getFieldValue(field) == null)
            throw new IllegalStateException("The field " + fieldInfo.getName() + " in " + fieldInfo.getClassName() + " must be initialized");
        return field;
    }

    /**
     * Generates a {@link Configuration} by processing annotations on a field.
     * Uses registered handlers from {@link ConfigHandlerRegistry} to interpret annotations.
     *
     * @param field the annotated field to process
     * @return a configuration populated with data from the field's annotations
     */
    public static Configuration createConfigurationFromField(@Nonnull Field field) {
        Configuration config = new Configuration();
        Annotation[] annotations = field.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            handleAnnotation(annotation, config);
        }
        return config;
    }

    @SuppressWarnings("unchecked") // Annotation must have a class of type T from type requirements
    private static <T extends Annotation> void handleAnnotation(@Nonnull T annotation, @Nonnull Configuration config) {
        if (!ConfigHandlerRegistry.hasValidHandler(annotation.annotationType())) return;
        ConfigHandlerRegistry.getHandler((Class<T>) annotation.annotationType()).process(annotation, config);
    }
}
