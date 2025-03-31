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
     * The key used for the entry in NetworkTables
     *
     * @return the string used for NetworkTables. Defaults to a table with the name of the class and name of field
     */
    String key() default "";

    /**
     * Type of NetworkTable Entry. (Publisher, Subscriber, Sendable)
     *
     * @return the type
     */
    EntryType type();
}

