package badgerlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark the configuration to use a specific struct publishing option.
 *
 * @see StructType
 */
@Target({ElementType.FIELD,  ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Struct {
    /**
     *
     * {@return the struct options to use for the field}
     */
    StructType value();
}
