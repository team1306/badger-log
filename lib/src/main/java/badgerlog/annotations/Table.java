package badgerlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the configuration to place an entry underneath the specified table instead of using the default one.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD,  ElementType.TYPE})
public @interface Table {
    /**
     * {@return the table to use instead of the generated one}
     */
    String value();
}
