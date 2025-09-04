package badgerlog.processing;

import badgerlog.annotations.Interceptor;
import badgerlog.annotations.Watcher;
import badgerlog.utilities.ErrorLogger;
import badgerlog.utilities.Validation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@Aspect
public class EventAspect {
    private boolean initialFieldPass = false;

    @After("execution(*.new(..)) && !within(edu.wpi.first..*) && !within(badgerlog.processing..*)")
    public void addAllEventMethods(JoinPoint thisJoinPoint) {
        Class<?> staticReference = thisJoinPoint.getSignature().getDeclaringType();
        Object workingClass = thisJoinPoint.getThis();

        Arrays.stream(staticReference.getDeclaredMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers()))
                .forEach(method -> delegateEventMethod(method, workingClass));

        if (!initialFieldPass) {
            Arrays.stream(staticReference.getDeclaredMethods())
                    .filter(method -> Modifier.isStatic(method.getModifiers()))
                    .forEach(method -> delegateEventMethod(method, workingClass));
            initialFieldPass = true;
        }
    }
    
    private void handleInterceptorMethod(Method method, Object workingClass) {
        if(!Validation.validateInterceptorMethod(method)) {
            return;
        }
        
        //todo handle interceptor
    }
    
    private void handleWatcherMethod(Method method, Object workingClass) {
        if(!Validation.validateWatcherMethod(method)) {
            return;
        }
        
        //todo handle watcher        
    }

    private void delegateEventMethod(Method method, Object workingClass){
        if(method.isAnnotationPresent(Watcher.class) && method.isAnnotationPresent(Interceptor.class)) {
            ErrorLogger.methodError(method, "is annotated with @Watcher and @Interceptor. When it can only be annotated with one");
            return;
        }

        if(method.isAnnotationPresent(Watcher.class)){
            handleWatcherMethod(method, workingClass);
        }
        else if(method.isAnnotationPresent(Interceptor.class)) {
            handleInterceptorMethod(method, workingClass);
        }
    }
    
}
