package frc.robot.util.dashboardv3.networktables;

import io.github.classgraph.FieldInfo;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class DashboardUtil {

    /**
     * Gets a field value
     * @param field the field to get from
     * @return the value of the field
     * @param <FieldValue> the type of the field
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows({IllegalAccessException.class, IllegalArgumentException.class})
    public static <FieldValue> FieldValue getFieldValue(Field field) {
        field.setAccessible(true);
        return (FieldValue) field.get(null);
    }

    /**
     * Set a field's value
     * @param field the field to set
     * @param value the value to set the field to
     * @param <FieldValue> the type of the field
     */
    @SneakyThrows({IllegalAccessException.class})
    public static <FieldValue> void setFieldValue(Field field, FieldValue value) {
        field.setAccessible(true);
        field.set(null, value);
    }

    /**
     * Ensure that a field is correctly created, for use as a Publisher, Subscriber, or Sendable
     * @param fieldInfo the info for the field
     * @return the loaded field
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
