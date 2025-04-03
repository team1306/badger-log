package badgerlog.networktables;

import badgerlog.entry.Entry;
import badgerlog.networktables.mappings.MappingType;
import io.github.classgraph.FieldInfo;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * Field utility class used for BadgerLog internally to manipulate {@link Field} values
 */
public final class DashboardUtil {

    private DashboardUtil() {
    }

    /**
     * Gets the value on the provided {@link Field}
     *
     * @param field        the Field to get from
     * @param <FieldValue> the type on the Field. Implicitly assumed that the type matches the type on the field
     * @return the value on the field, cast to the provided type
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows({IllegalAccessException.class, IllegalArgumentException.class})
    public static <FieldValue> FieldValue getFieldValue(Field field) {
        field.setAccessible(true);
        return (FieldValue) field.get(null);
    }

    /**
     * Sets the value on the provided {@link Field} to the specified value
     *
     * @param field        the Field to set
     * @param value        the value to set the Field to
     * @param <FieldValue> the type on the Field. Implicitly assumed that the type matches the type on the field
     */
    @SneakyThrows({IllegalAccessException.class})
    public static <FieldValue> void setFieldValue(Field field, FieldValue value) {
        field.setAccessible(true);
        field.set(null, value);
    }

    /**
     * Verify that the provided {@link FieldInfo} matches the requirements for a field annotated with {@link Entry} or {@link MappingType}
     *
     * @param fieldInfo the FieldInfo for the field
     * @return the loaded {@link Field}
     */
    public static Field checkFieldValidity(FieldInfo fieldInfo) {
        var field = fieldInfo.loadClassAndGetField();
        if (!fieldInfo.isStatic())
            throw new IllegalArgumentException("The field " + fieldInfo.getName() + " in " + fieldInfo.getClassName() + " must be static");
        if (fieldInfo.isFinal())
            throw new IllegalArgumentException("The field " + fieldInfo.getName() + " in " + fieldInfo.getClassName() + " must not be final");
        if (DashboardUtil.getFieldValue(field) == null)
            throw new IllegalArgumentException("The field " + fieldInfo.getName() + " in " + fieldInfo.getClassName() + " must be initialized");
        return field;
    }
}
