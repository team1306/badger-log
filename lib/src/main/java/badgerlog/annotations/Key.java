package badgerlog.annotations;

import badgerlog.annotations.configuration.Configurable;
import badgerlog.annotations.configuration.KeyHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify a configuration NetworkTables key for a field.
 *
 * @see KeyHandler
 */
@Configurable
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Key {
    /**
     * The key string to use for NetworkTables
     *
     * @return the key
     */
    String value();
}
