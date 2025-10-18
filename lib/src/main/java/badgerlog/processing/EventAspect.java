package badgerlog.processing;

import badgerlog.annotations.RawWatcher;
import badgerlog.annotations.Watcher;
import badgerlog.events.EventMetadata;
import badgerlog.events.EventRegistry;
import badgerlog.events.WatcherEvent;
import badgerlog.utilities.Members;
import badgerlog.utilities.Validation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;
import java.util.Map;

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

        Members.iterateOverAnnotatedMethods(clazz, Watcher.class, true, method -> handleWatcherMethod(method, null));
        Members.iterateOverAnnotatedMethods(clazz, RawWatcher.class, true, method -> handleRawWatcherMethod(method, null));
    }

    @After("onlyRobotCode() && newInitialization()")
    public void createInstanceEvents(JoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getSignature().getDeclaringType();
        Object workingClass = joinPoint.getThis();

        Members.iterateOverAnnotatedMethods(clazz, Watcher.class, true, method -> handleWatcherMethod(method, workingClass));
        Members.iterateOverAnnotatedMethods(clazz, RawWatcher.class, true, method -> handleRawWatcherMethod(method, workingClass));
    }

    private void handleWatcherMethod(Method method, Object workingClass) {
        if (!Validation.validateWatcherMethod(method)) {
            return;
        }

        Watcher watcher = method.getAnnotation(Watcher.class);
        
        EventMetadata metadata = new EventMetadata(watcher.keys(), watcher.name(), watcher.eventType());
        WatcherEvent<?> event = new WatcherEvent<>(getPrimitiveType(watcher.type()), (data) -> Members.invokeMethod(method, workingClass, data));
        EventRegistry.registerWatcher(event, metadata);
    }

    private void handleRawWatcherMethod(Method method, Object workingClass) {
        if (!Validation.validateWatcherMethod(method)) {
            return;
        }

        RawWatcher watcher = method.getAnnotation(RawWatcher.class);

        EventMetadata metadata = new EventMetadata(watcher.keys(), null, watcher.eventType());
        WatcherEvent<?> event = new WatcherEvent<>(getPrimitiveType(watcher.type()), (data) -> Members.invokeMethod(method, workingClass, data));
        EventRegistry.registerRawWatcher(event, metadata);
    }

    public static Class<?> getPrimitiveType(Class<?> type) {
        var boxedToPrimitive = Map.ofEntries(
                Map.entry(Integer.class, int.class),
                Map.entry(Long.class, long.class),
                Map.entry(Double.class, double.class),
                Map.entry(Boolean.class, boolean.class),
                Map.entry(Float.class, float.class),
                Map.entry(Byte.class, byte.class),
                Map.entry(Short.class, short.class),
                Map.entry(Character.class, char.class),
                Map.entry(void.class, void.class)
        );

        return boxedToPrimitive.getOrDefault(type, type);
    }
}
