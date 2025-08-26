package badgerlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark the configuration to use a specific struct publishing option.
 *
 * @see StructOptions
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StructType {
    /**
     *
     * {@return the struct options to use for the field}
     */
    StructOptions value();
}
