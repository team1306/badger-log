package badgerlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field to be collected and have NetworkTables entries created for it.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Entry {
    /**
     * {@return the type of entry the field should use}
     */
    EntryType value();
}

