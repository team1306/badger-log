package badgerlog.entry.handlers;

import badgerlog.entry.Configurable;
import badgerlog.networktables.mappings.conversion.UnitConversions;

import java.lang.annotation.*;

/**
 * Annotation to define a unit conversion for field values.
 * Can be used repeatedly via {@link MultiUnitConversion} container.
 *
 * @see UnitConversionHandler
 */
@Configurable
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(MultiUnitConversion.class)
public @interface UnitConversion {
    /**
     * Unit conversion specification string
     *
     * @return format defined by {@link UnitConversions}
     */
    String value();

    /**
     * Unique identifier for the converter (empty for default)
     *
     * @return converter ID string
     */
    String converterId() default "";
}
