package badgerlog.entry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used for publishing or subscribing to a value from NetworkTables
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Entry {
    /**
     * Type of NetworkTable Entry. (Publisher, Subscriber, Sendable)
     *
     * @return the entry type
     * @see EntryType
     */
    EntryType value();
}

