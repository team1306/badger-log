package badgerlog.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the configuration to use the specified converter.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Repeatable(MultiUnitConversion.class)
public @interface UnitConversion {
    /**
     * {@return the String representation of the unit to use to convert with}
     */
    String value();

    /**
     * {@return the Id of the converter to use} Leave blank to use the default id
     */
    String converterId() default "";
}
