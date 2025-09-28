package badgerlog.utilities;

import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Simplifies the invoking of methods.
 */
public class Methods {
    
    /**
     * Gets a list of declared methods within a class that are annotated by {@code annotationClass}
     * @param clazz the class to get the methods from
     * @param annotationClass the annotation to check the methods for
     * @return an array of the methods annotated with the specified annotation
     */
    public static Method[] getMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass){
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .toArray(Method[]::new);
    }

    /**
     * Runs a specific method with the specified arguments.
     * @param method the method to invoke
     * @param instance the instance of the class for instance methods
     * @param args the arguments to be passed to the method
     * @return the return value of the method
     */
    @SneakyThrows({IllegalAccessException.class, InvocationTargetException.class})
    public static Object invokeMethod(Method method, Object instance, Object... args){
        method.setAccessible(true);
        return method.invoke(instance, args);
    }
}
