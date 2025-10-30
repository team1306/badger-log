package badgerlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field to be part of the existing managed watcher events
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Watched {
    /**
     * {@return the array of event names to be watched by}
     */
    String[] value();
}
