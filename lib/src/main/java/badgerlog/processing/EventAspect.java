package badgerlog.processing;

import badgerlog.annotations.EventType;
import badgerlog.annotations.Interceptor;
import badgerlog.annotations.Watcher;
import badgerlog.events.EventMetadata;
import badgerlog.events.EventRegistry;
import badgerlog.events.InterceptorEvent;
import badgerlog.events.WatcherEvent;
import badgerlog.utilities.ErrorLogger;
import badgerlog.utilities.Methods;
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

    @SuppressWarnings("unchecked")
    private void handleInterceptorMethod(Method method, Object workingClass) {
        if (!Validation.validateInterceptorMethod(method)) {
            return;
        }
        Interceptor interceptor = method.getAnnotation(Interceptor.class);

        EventMetadata metadata = new EventMetadata(interceptor.keys(), interceptor.name(), EventType.ALL, interceptor.priority());
        InterceptorEvent<?> event = new InterceptorEvent<>((Class<Object>) method.getReturnType(), (data) -> Methods.invokeMethod(method, workingClass, data));
        EventRegistry.registerInterceptor(event, metadata);
    }

    @SuppressWarnings("unchecked")
    private void handleWatcherMethod(Method method, Object workingClass) {
        if (!Validation.validateWatcherMethod(method)) {
            return;
        }

        Watcher watcher = method.getAnnotation(Watcher.class);
        
        EventMetadata metadata = new EventMetadata(watcher.keys(), watcher.name(), watcher.eventType(), 0);
        WatcherEvent<?> event = new WatcherEvent<>((Class<Object>) method.getReturnType(), (data) -> Methods.invokeMethod(method, workingClass, data));
        EventRegistry.registerWatcher(event, metadata);
    }

    private void delegateEventMethod(Method method, Object workingClass) {
        if (method.isAnnotationPresent(Watcher.class) && method.isAnnotationPresent(Interceptor.class)) {
            ErrorLogger
                    .memberError(method, "is annotated with both @Watcher and @Interceptor. When it can only be annotated with one");
            return;
        }

        if (method.isAnnotationPresent(Watcher.class)) {
            handleWatcherMethod(method, workingClass);
        } else if (method.isAnnotationPresent(Interceptor.class)) {
            handleInterceptorMethod(method, workingClass);
        }
    }

}
