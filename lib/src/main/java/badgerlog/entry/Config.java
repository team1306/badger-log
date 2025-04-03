package badgerlog.entry;

import badgerlog.networktables.mappings.Mapping;
import badgerlog.networktables.mappings.Mappings;
import badgerlog.networktables.mappings.UnitMappings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration annotation for an {@link Entry}. Configuration options for the {@link #value()} parameter of the annotation can be found where the respective {@link Mapping} was registered
 *
 * @see UnitMappings
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Config {
    /**
     * The configuration option value as a string for use in mappings
     *
     * @return the string config option
     * @see Mappings
     */
    String value();
}
