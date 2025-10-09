package badgerlog.processing;

import badgerlog.annotations.Watcher;
import badgerlog.events.EventMetadata;
import badgerlog.events.EventRegistry;
import badgerlog.events.WatcherEvent;
import badgerlog.utilities.Methods;
import badgerlog.utilities.Validation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

@Aspect
public class EventAspect {

    @Pointcut("!within(edu.wpi.first..*) && !within(badgerlog..*) && !within(java..*) && !within(javax..*)")
    public void onlyRobotCode() {
    }

    @Pointcut("execution(*.new(..))")
    public void newInitialization() {
    }

    @After("onlyRobotCode() && staticinitialization(*)")
    public void createStaticEvents(JoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getSignature().getDeclaringType();
        
        Method[] methods = Methods.getMethodsWithAnnotation(clazz, Watcher.class);
        Arrays.stream(methods)
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .forEach(method -> handleWatcherMethod(method, null));
    }

    @After("onlyRobotCode() && newInitialization()")
    public void createInstanceEvents(JoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getSignature().getDeclaringType();
        Object workingClass = joinPoint.getThis();
        
        Method[] methods = Methods.getMethodsWithAnnotation(clazz, Watcher.class);
        Arrays.stream(methods)
                .filter(method -> !Modifier.isStatic(method.getModifiers()))
                .forEach(method -> handleWatcherMethod(method, workingClass));
    }

    private void handleWatcherMethod(Method method, Object workingClass) {
        if (!Validation.validateWatcherMethod(method)) {
            return;
        }

        Watcher watcher = method.getAnnotation(Watcher.class);
        
        EventMetadata metadata = new EventMetadata(watcher.keys(), watcher.name(), watcher.eventType());
        WatcherEvent<?> event = new WatcherEvent<>(watcher.type(), (data) -> Methods.invokeMethod(method, workingClass, data));
        EventRegistry.registerWatcher(event, metadata);
    }
}
