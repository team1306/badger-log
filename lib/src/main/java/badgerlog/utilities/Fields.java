package badgerlog.utilities;

import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Internal class used by BadgerLog to simplify the accessing of fields with reflection.
 */
public final class Fields {

    private Fields() {
    }

    public static Field[] getFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass, boolean instance){
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .filter(field -> instance != Modifier.isStatic(field.getModifiers()))
                .toArray(Field[]::new);
    }
    
    /**
     * {@code object} defaults to {@code null}. This method should only be used if the field is known to be static.
     *
     * @see #getFieldValue(Field, Object)
     */
    public static Object getFieldValue(Field field) {
        return getFieldValue(field, null);
    }

    /**
     * Gets the value currently on the field, ignoring access modifiers.
     *
     * @param field the field to access
     * @param object the instance to access the field with
     *
     * @return the value on the field in the specific instance
     */
    @SneakyThrows({IllegalAccessException.class, IllegalArgumentException.class})
    public static Object getFieldValue(Field field, Object object) {
        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * Sets the field's value to the specified value, ignoring access modifiers.
     *
     * @param instance the instance to set the field with
     * @param field the field to set
     * @param value the value to set on the field
     */
    @SneakyThrows({IllegalAccessException.class})
    public static void setFieldValue(Object instance, Field field, Object value) {
        field.setAccessible(true);
        field.set(instance, value);
    }
}
