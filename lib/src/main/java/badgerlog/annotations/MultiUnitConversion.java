package badgerlog.annotations;

import badgerlog.annotations.configuration.Configurable;
import badgerlog.annotations.configuration.MultiUnitConversionHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation for multiple {@link UnitConversion} declarations.
 * Allows grouping of unit conversions for a single field.
 *
 * @see MultiUnitConversionHandler
 */
@Configurable
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MultiUnitConversion {
    /**
     * Array of unit conversion specifications
     *
     * @return non-empty array of {@link UnitConversion} annotations
     */
    UnitConversion[] value();
}
