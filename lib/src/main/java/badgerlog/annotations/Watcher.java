package badgerlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a managed event
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Watcher {
    /**
     * {@return the type of the event}
     */
    Class<?> type();

    /**
     * {@return the name of the event for fields}
     */
    String name();
}
