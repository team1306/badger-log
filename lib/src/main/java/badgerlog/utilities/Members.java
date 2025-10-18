package badgerlog.utilities;

import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Simplifies accessing of members and their modifiers
 */
public class Members {
    private Members() {}
    
    /**
     * Gets a list of declared fields within a class that are annotated by {@code annotationClass}
     *
     * @param clazz the class to get the fields from
     * @param annotationClass the annotation to check the fields for
     *
     * @return an array of the fields annotated with the specified annotation
     */
    public static Field[] getFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .toArray(Field[]::new);
    }
    
    public static void iterateOverAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotationClass, boolean isStatic, Consumer<Field> consumer) {
        Field[] fields = getFieldsWithAnnotation(clazz, annotationClass);
        Arrays.stream(fields)
                .filter(field -> isStatic == isMemberStatic(field))
                .forEach(consumer);
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
    
    /**
     * Gets a list of declared methods within a class that are annotated by {@code annotationClass}
     *
     * @param clazz the class to get the methods from
     * @param annotationClass the annotation to check the methods for
     *
     * @return an array of the methods annotated with the specified annotation
     */
    public static Method[] getMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .toArray(Method[]::new);
    }

    /**
     * Runs a specific method with the specified arguments.
     *
     * @param method the method to invoke
     * @param instance the instance of the class for instance methods
     * @param args the arguments to be passed to the method
     *
     * @return the return value of the method
     */
    @SneakyThrows({IllegalAccessException.class, InvocationTargetException.class})
    public static Object invokeMethod(Method method, Object instance, Object... args) {
        method.setAccessible(true);
        return method.invoke(instance, args);
    }

    public static void iterateOverAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotationClass, boolean isStatic, Consumer<Method> consumer) {
        Method[] methods = getMethodsWithAnnotation(clazz, annotationClass);
        Arrays.stream(methods)
                .filter(method -> isStatic == isMemberStatic(method))
                .forEach(consumer);
    }

    public static boolean isMemberStatic(Member field) {
        return Modifier.isStatic(field.getModifiers());
    }

    public static boolean isMemberNonStatic(Member field) {
        return !isMemberStatic(field);
    }
}
