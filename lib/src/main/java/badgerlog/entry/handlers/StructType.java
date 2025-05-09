package badgerlog.entry.handlers;

import badgerlog.StructOptions;
import badgerlog.entry.Configurable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify struct entry options for a field's configuration.
 * Configures publishing/subscribing behavior via {@link StructOptions}.
 *
 * @see StructTypeHandler
 */
@Configurable
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StructType {
    /**
     * Struct configuration options
     *
     * @return {@link StructOptions} enum value
     */
    StructOptions value();
}
