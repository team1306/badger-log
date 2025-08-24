package badgerlog.processing;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

public final class Fields {

    private Fields() {
    }

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
