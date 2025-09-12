package badgerlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to allow the {@link UnitConversion} annotation to appear multiple times on a field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface MultiUnitConversion {
    /**
     *
     * {@return the array of UnitConversion annotations}
     */
    UnitConversion[] value();
}
