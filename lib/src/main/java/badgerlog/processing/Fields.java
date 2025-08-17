package badgerlog.processing;

import badgerlog.annotations.configuration.Configuration;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * Utility class for internal BadgerLog operations involving field manipulation via reflection.
 * Provides methods to get/set static field values, validate field constraints (static, non-final, initialized),
 * and generate {@link Configuration} objects by processing field annotations using registered handlers.
 * <p>
 * This class is not meant to be instantiated and operates exclusively using static methods.
 */
public final class Fields {

    private Fields() {
    }

    /**
     * Retrieves the value of a static field via reflection.
     *
     * @param field the static field to read
     * @return the current value of the field
     */
    public static Object getFieldValue(Field field) {
        return getFieldValue(field, null);
    }

    @SneakyThrows({IllegalAccessException.class, IllegalArgumentException.class})
    public static Object getFieldValue(Field field, Object object) {
        field.setAccessible(true);
        return field.get(object);
    }

    @SneakyThrows({IllegalAccessException.class})
    public static void setFieldValue(Object instance, Field field, Object value) {
        field.setAccessible(true);
        field.set(instance, value);
    }
}
