package badgerlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an element to have a struct auto-generated for it.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface AutoGenerateStruct {
    /**
     * {@return whether or not to use automatic struct generation}
     */
    boolean autoGenerateStruct() default true;
}
