package badgerlog.annotations;

import java.lang.annotation.*;

/**
 * Annotation to mark the configuration to use a specified converter.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
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
