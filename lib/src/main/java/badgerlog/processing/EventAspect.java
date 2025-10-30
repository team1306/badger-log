package badgerlog.processing;

import badgerlog.annotations.EventType;
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

/**
 * Utilizes AspectJ to weave event initialization into target classes
 */
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

        Members.iterateOverAnnotatedMethods(clazz, Watcher.class, false, method -> handleWatcherMethod(method, workingClass));
        Members.iterateOverAnnotatedMethods(clazz, RawWatcher.class, false, method -> handleRawWatcherMethod(method, workingClass));
    }

    private void handleWatcherMethod(Method method, Object workingClass) {
        if (!Validation.validateWatcherMethod(method)) {
            return;
        }

        Watcher watcher = method.getAnnotation(Watcher.class);

        EventMetadata metadata = new EventMetadata(null, watcher.name(), EventType.ALL);
        WatcherEvent<?> event = new WatcherEvent<>(getObjectType(watcher.type()), (data) -> Members
                .invokeMethod(method, workingClass, data));
        EventRegistry.registerWatcher(event, metadata);
    }

    private void handleRawWatcherMethod(Method method, Object workingClass) {
        if (!Validation.validateWatcherMethod(method)) {
            return;
        }

        RawWatcher watcher = method.getAnnotation(RawWatcher.class);

        EventMetadata metadata = new EventMetadata(watcher.keys(), null, watcher.eventType());
        WatcherEvent<?> event = new WatcherEvent<>(getObjectType(watcher.type()), (data) -> Members
                .invokeMethod(method, workingClass, data));
        EventRegistry.registerRawWatcher(event, metadata);
    }

    public static Class<?> getObjectType(Class<?> type) {
        var boxedToPrimitive = Map.ofEntries(
                Map.entry(int.class, Integer.class), Map.entry(long.class, Long.class), Map
                        .entry(double.class, Double.class), Map.entry(boolean.class, Boolean.class), Map
                                .entry(float.class, Float.class), Map.entry(byte.class, Byte.class), Map
                                        .entry(short.class, Short.class), Map.entry(char.class, Character.class), Map
                                                .entry(void.class, void.class)
        );

        return boxedToPrimitive.getOrDefault(type, type);
    }
}
