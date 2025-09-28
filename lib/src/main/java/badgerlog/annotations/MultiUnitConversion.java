package badgerlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows the {@link UnitConversion} annotation to appear multiple times on an element.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface MultiUnitConversion {
    /**
     * {@return the array of UnitConversion annotations}
     */
    UnitConversion[] value();
}
