package badgerlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a raw, unmanaged NetworkTables watcher
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RawWatcher {
    /**
     * {@return the value type of the event}
     */
    Class<?> type();

    /**
     * {@return the condition when the event should fire}
     */
    EventType eventType() default EventType.ALL;

    /**
     * {@return an array of NetworkTables keys to watch for value changes}
     */
    String[] keys();
}
