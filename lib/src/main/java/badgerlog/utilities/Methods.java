package badgerlog.utilities;

import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class Methods {
    @SneakyThrows({IllegalAccessException.class, InvocationTargetException.class})
    public static Object invokeMethod(Method method, Object instance, Object... args){
        method.setAccessible(true);
        return method.invoke(instance, args);
    }

    public static Method[] getMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass){
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .toArray(Method[]::new);
    }
}
