package badgerlog.processing;

import badgerlog.annotations.Entry;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Field;

@SuppressWarnings("-javadoc")
@Aspect
public class EntryAspect {

    @Pointcut("execution(*.new(..)) && if()")
    public static boolean constructorWithEntryAnnotationSomewhere(JoinPoint thisJoinPoint) {
        return hasFieldEntryAnnotation(thisJoinPoint.getSignature().getDeclaringType());
    }

    private static boolean hasFieldEntryAnnotation(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Entry.class)) {
                return true;
            }
        }

        for (Field field : clazz.getFields()) {
            if (field.isAnnotationPresent(Entry.class)) {
                return true;
            }
        }
        return false;
    }

    @After("constructorWithEntryAnnotationSomewhere")
    public void addAllEntryFields(JoinPoint thisJoinPoint) {
        Class<?> staticReference = thisJoinPoint.getSignature().getDeclaringType();
        Object workingClass = thisJoinPoint.getThis();

        if (workingClass != null) handleInstanceFields(workingClass);
        if (staticReference != null) handleStaticFields(staticReference);
    }

    private void handleStaticFields(Class<?> clazz) {

    }

    private void handleInstanceFields(Object instance) {

    }
}
